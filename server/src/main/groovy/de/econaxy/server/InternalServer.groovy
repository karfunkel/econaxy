package de.econaxy.server

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class InternalServer {
    @Delegate
    Server server

    protected ServletContextHandler context

    protected Logger log = LoggerFactory.getLogger(InternalServer)

    void init(int port, Logger log = null) {
        server = new Server(port)
        context = new ServletContextHandler(ServletContextHandler.SESSIONS)
        context.contextPath = "/"
        server.handler = context
        context.addServlet(new ServletHolder(new EconaxyServlet()), "/*")
        server.stopAtShutdown = true
    }

    void run() {
        server.start()
        server.join()
    }

}
