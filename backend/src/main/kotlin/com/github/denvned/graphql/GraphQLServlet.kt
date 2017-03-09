package com.github.denvned.graphql

import com.fasterxml.jackson.module.kotlin.readValue
import javax.inject.Inject
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet("/")
class GraphQLServlet : HttpServlet() {
  @Inject private lateinit var graphQLExecutor: GraphQLExecutor

  override fun doGet(req: HttpServletRequest, res: HttpServletResponse) {
    res.setResponse(graphQLExecutor.execute(req.getParameter(Companion.QUERY_PARAMETER)))
  }

  override fun doPost(req: HttpServletRequest, res: HttpServletResponse) {
    mapper.readValue<GraphQLRequest>(req.reader).let {
      res.setResponse(graphQLExecutor.execute(it))
    }
  }

  companion object {
    const val QUERY_PARAMETER = "q"

    private fun HttpServletResponse.setResponse(payload: GraphQLResponse) {
      payload.errors?.let { status = 500 }
      contentType = "application/json"
      writer.write(mapper.writeValueAsString(payload))
    }
  }
}
