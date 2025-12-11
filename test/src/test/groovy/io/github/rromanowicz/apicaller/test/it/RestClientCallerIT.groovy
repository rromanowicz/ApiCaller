package io.github.rromanowicz.apicaller.test.it


import io.github.rromanowicz.apicaller.incomming.IncomingRequestLoggingFilter
import io.github.rromanowicz.apicaller.restclient.config.LoggingInterceptor
import io.github.rromanowicz.apicaller.test.config.SpockIT
import io.restassured.RestAssured
import io.restassured.http.ContentType
import spock.lang.Stepwise
import spock.lang.Title

@Stepwise
@Title("RestClient")
class RestClientCallerIT extends SpockIT {

    def "GET all 200"() {
        given:
        def incomingLogs = getLogs(IncomingRequestLoggingFilter.class)
        def outgoingLogs = getLogs(LoggingInterceptor.class)
        def request = RestAssured.with()
                .port(port)

        when:
        def response = request.get("/v1/test")

        then:
        assert response
        assert incomingLogs.list.size() == 1
        assert outgoingLogs.list.size() == 1

        def incoming = parseIncomingLog(incomingLogs.list.get(0).message)
        assert incoming.incomingRequest().method() == "GET"
        assert incoming.incomingRequest().url() == "/v1/test"

        def outgoing = parseOutgoingLog(outgoingLogs.list.get(0).message)
        assert outgoing.request().method() == "GET"
        assert outgoing.request().url().endsWith("/users")
        assert outgoing.response().status() == 200
    }

    def "GET byId 200"() {
        given:
        def incomingLogs = getLogs(IncomingRequestLoggingFilter.class)
        def outgoingLogs = getLogs(LoggingInterceptor.class)
        def request = RestAssured.with()
                .port(port)

        when:
        def response = request.get("/v1/test/1?param=Test")

        then:
        assert response
        assert incomingLogs.list.size() == 1
        assert outgoingLogs.list.size() == 1

        def incoming = parseIncomingLog(incomingLogs.list.get(0).message)
        assert incoming.incomingRequest().method() == "GET"
        assert incoming.incomingRequest().url() == "/v1/test/1"
        assert incoming.incomingRequest().query() == "param=Test"

        def outgoing = parseOutgoingLog(outgoingLogs.list.get(0).message)
        assert outgoing.request().method() == "GET"
        assert outgoing.request().url().endsWith("/users/1")
        assert outgoing.response().status() == 200

    }


    def "GET byId 404"() {
        given:
        def incomingLogs = getLogs(IncomingRequestLoggingFilter.class)
        def outgoingLogs = getLogs(LoggingInterceptor.class)
        def request = RestAssured.with()
                .port(port)

        when:
        def response = request.get("/v1/test/9?param=Test")

        then:
        assert response
        assert incomingLogs.list.size() == 1
        assert outgoingLogs.list.size() == 1

        def incoming = parseIncomingLog(incomingLogs.list.get(0).message)
        assert incoming.incomingRequest().method() == "GET"
        assert incoming.incomingRequest().url() == "/v1/test/9"
        assert incoming.incomingRequest().query() == "param=Test"

        def outgoing = parseOutgoingLog(outgoingLogs.list.get(0).message)
        assert outgoing.request().method() == "GET"
        assert outgoing.request().url().endsWith("/users/9")
        assert outgoing.response().status() == 404

    }

    def "POST 201"() {
        given:
        def incomingLogs = getLogs(IncomingRequestLoggingFilter.class)
        def outgoingLogs = getLogs(LoggingInterceptor.class)
        def request = RestAssured.with()
                .port(port)
                .contentType(ContentType.JSON)
                .body("""
                  {
                    "id": 567,
                    "username": "Test",
                    "email": "test@zxc.com",
                    "password": "ASD234"
                  }
                """)

        when:
        def response = request.post("/v1/test")

        then:
        assert response
        assert response.statusCode() == 200
        assert incomingLogs.list.size() == 1
        assert outgoingLogs.list.size() == 1

        def incoming = parseIncomingLog(incomingLogs.list.get(0).message)
        assert incoming.incomingRequest().method() == "POST"
        assert incoming.incomingRequest().url() == "/v1/test"

        def outgoing = parseOutgoingLog(outgoingLogs.list.get(0).message)
        assert outgoing.response().status() == 201
        assert outgoing.request().method() == "POST"
        assert outgoing.request().url().endsWith("/users")
    }

    def "POST 500"() {
        given:
        def incomingLogs = getLogs(IncomingRequestLoggingFilter.class)
        def outgoingLogs = getLogs(LoggingInterceptor.class)
        def request = RestAssured.with()
                .port(port)
                .contentType(ContentType.JSON)
                .body("""
                  {
                    "id": 500,
                    "username": "Test",
                    "email": "test@zxc.com",
                    "password": "ASD234"
                  }
                """)

        when:
        def response = request.post("/v1/test")

        then:
        assert response
        assert incomingLogs.list.size() == 1
        assert outgoingLogs.list.size() == 1

        def incoming = parseIncomingLog(incomingLogs.list.get(0).message)
        assert incoming.incomingRequest().method() == "POST"
        assert incoming.incomingRequest().url() == "/v1/test"

        def outgoing = parseOutgoingLog(outgoingLogs.list.get(0).message)
        assert outgoing.response().status() == 500
        assert outgoing.request().method() == "POST"
        assert outgoing.request().url().endsWith("/users")

    }
}
