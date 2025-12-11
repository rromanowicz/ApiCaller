#!/bin/bash

git checkout master 2>&1 > /dev/null
git pull 2>&1 > /dev/null
MASTER_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
# MASTER_VERSION="0.0.1"

git merge-base --is-ancestor HEAD develop
RETVAL=$?
if [ $RETVAL -ne 0 ]; then
  echo "Master and develop don't share history."
  exit 1
fi
echo "Merge check (develop --> master): OK"

git checkout develop 2>&1 > /dev/null
git pull 2>&1 > /dev/null
CURRENT_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
# CURRENT_VERSION="0.0.2-SNAPSHOT"

echo "Last release version: $MASTER_VERSION"
echo "Current version: $CURRENT_VERSION"

while :
do
  echo
  echo "Select release type:"
  echo "  1 - Major"
  echo "  2 - Minor"
  echo "  3 - Fix"
  echo "  0 - Abort"
  read -p "> " REL_TYPE
  if [[ $REL_TYPE =~ ^[0123]{1}$ ]]; then
    if [[ $REL_TYPE == 0 ]]; then
      echo "Release aborted."
      exit 0
    fi
    break
  else
    echo "Invalid option..."
  fi
done

IFS='-' read -ra VER <<< "$CURRENT_VERSION"
RELEASE_VERSION=${VER[0]}


IFS='.' read -ra VERS <<< "$RELEASE_VERSION"
MAJOR=${VERS[0]}
MINOR=${VERS[1]}
FIX=${VERS[2]}

if [[ $REL_TYPE == 1 ]]; then
  MAJOR=$(($MAJOR+1))
  MINOR=0
  FIX=0
fi
if [[ $REL_TYPE == 2 ]]; then
  MINOR=$(($MINOR+1))
  FIX=0
fi

RELEASE_VERSION="$MAJOR.$MINOR.$FIX"
FIX=$(($FIX+1))
NEXT_SNAPSHOT_VERSION="$MAJOR.$MINOR.$FIX-SNAPSHOT"

while :
do
  echo
  echo "Are you sure you want to release: $MASTER_VERSION --> $RELEASE_VERSION?"
  echo "(Y/N)"
  read -p "> " ACCEPTED
  if [[ $ACCEPTED =~ ^[YyNn]{1}$ ]]; then
    if [[ $ACCEPTED == ^[Nn]{1}$ ]]; then
      echo "Release aborted."
      exit 1
    fi
    break
  else
    echo "Invalid option..."
  fi
done

echo
echo "Releasing $RELEASE_VERSION"
git checkout -b "release-$RELEASE_VERSION"
mvn versions:set -DnewVersion=$RELEASE_VERSION 2>&1 > /dev/null
mvn versions:commit
git add "./*pom.xml*"
git commit -m "Release-$RELEASE_VERSION"

git checkout master
git merge "release-$RELEASE_VERSION"
git push
echo "'master' updated. $MASTER_VERSION --> $RELEASE_VERSION"

git checkout -b "master-to-develop-$RELEASE_VERSION"
mvn versions:set -DnewVersion=$NEXT_SNAPSHOT_VERSION 2>&1 > /dev/null
mvn versions:commit
git add "./*pom.xml*"
git commit -m "Snapshot-$NEXT_SNAPSHOT_VERSION"

git checkout develop
git merge "master-to-develop-$RELEASE_VERSION"
git push
echo "'develop' updated. $CURRENT_VERSION --> $NEXT_SNAPSHOT_VERSION"

git checkout master
git tag $RELEASE_VERSION
git push origin $RELEASE_VERSION
echo "Tag $RELEASE_VERSION created."

git checkout develop
