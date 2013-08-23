package org.ualerts.demo.servlet;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import demo.GreetingService;

@WebServlet("/hello")
public class GreetingServlet extends HttpServlet {

  private static final long serialVersionUID = -2700408606239725576L;

  @EJB
  private GreetingService greetingService;
  
  /**
   * {@inheritDoc}
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.setContentType("text/plain");
    response.getWriter().println(greetingService.generateGreeting("Cherylanne"));
  }
  
}
