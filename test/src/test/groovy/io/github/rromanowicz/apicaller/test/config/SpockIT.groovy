package io.github.rromanowicz.apicaller.test.config

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import com.fasterxml.jackson.core.type.TypeReference
import io.github.rromanowicz.apicaller.common.Model
import io.github.rromanowicz.apicaller.core.ApiCaller
import io.github.rromanowicz.apicaller.test.RestClientController
import io.github.rromanowicz.apicaller.test.TestService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import spock.lang.Specification

import static io.github.rromanowicz.apicaller.common.Util.asObject

@AutoConfigureWireMock(port = 0, stubs = "classpath*:/stubs/**/*.json")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class SpockIT extends Specification {

    @LocalServerPort
    protected int port

    @Autowired
    @Qualifier("uniRestCaller")
    private ApiCaller uniRestCaller
    @Autowired
    @Qualifier("restClientCaller")
    private ApiCaller restClientCaller

    @Autowired
    protected TestService testService

    @Autowired
    protected RestClientController restClientController


    protected <T> ListAppender<ILoggingEvent> getLogs(Class<T> clazz) {
        Logger logger = LoggerFactory.getLogger(clazz)
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>()
        listAppender.start()
        logger.addAppender(listAppender)
        return listAppender
    }

    Model.OutgoingLog parseOutgoingLog(Object obj) {
        return asObject((String) obj, new TypeReference<Model.OutgoingLog>() {})
    }

    Model.IncomingLog parseIncomingLog(Object obj) {
        return asObject((String) obj, new TypeReference<Model.IncomingLog>() {})
    }


}
