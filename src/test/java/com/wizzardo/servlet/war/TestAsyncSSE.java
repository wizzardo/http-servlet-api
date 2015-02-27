package com.wizzardo.servlet.war;

import com.wizzardo.servlet.WarBuilder;
import com.wizzardo.servlet.WarTest;
import org.junit.Test;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wizzardo on 27.02.15.
 */
public class TestAsyncSSE extends WarTest {

    @Override
    protected void customizeWar(WarBuilder builder) {
        servletPath = "/sseasync";
        builder.addClass(HtmlServlet.class);
        builder.addClass(SSEAsyncServlet.class);
        builder.getWebXmlBuilder()
                .append(new WarBuilder.ServletMapping(HtmlServlet.class).url("/"))
                .append(new WarBuilder.ServletMapping(SSEAsyncServlet.class).url("/sseasync").enableAsync())
        ;
    }

    @Test
    public void testOk() throws IOException {
//        test(request -> request.get().asString());
        try {
            Thread.sleep(10 * 60 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static class HtmlServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            resp.getWriter().write("<html>\n" +
                    "  <body>\n" +
                    "    <script>\n" +
                    "      function setupEventSource() {\n" +
                    "        var output = document.getElementById(\"output\");\n" +
                    "        if (typeof(EventSource) !== \"undefined\") {\n" +
                    "          var msg = document.getElementById(\"textID\").value;\n" +
                    "          var source = new EventSource(\"sseasync?msg=\" + msg);\n" +
                    "          source.onmessage = function(event) {\n" +
                    "            output.innerHTML += event.data + \"<br>\";\n" +
                    "          };\n" +
                    "          source.addEventListener('close', function(event) {\n" +
                    "            output.innerHTML += event.data + \"<hr/>\";\n" +
                    "            source.close();\n" +
                    "            }, false);\n" +
                    "        } else {\n" +
                    "          output.innerHTML = \"Sorry, Server-Sent Events are not supported in your browser\";\n" +
                    "        }\n" +
                    "        return false;\n" +
                    "      }\n" +
                    "    </script>\n" +
                    "\n" +
                    "    <h2>SSE Echo Demo</h2>\n" +
                    "    <div>\n" +
                    "      <input type=\"text\" id=\"textID\" name=\"message\" value=\"Hello World\">\n" +
                    "      <input type=\"button\" id=\"sendID\" value=\"Send\" onclick=\"setupEventSource()\"/>\n" +
                    "    </div>\n" +
                    "    <hr/> \n" +
                    "    <div id=\"output\"></div>\n" +
                    "  </body> \n" +
                    "</html>    ");
        }
    }

//    @WebServlet(urlPatterns = {"/sseasync"}, asyncSupported = true)
    public static class SSEAsyncServlet extends HttpServlet {

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse res)
                throws IOException, ServletException {

            // set content type
            res.setContentType("text/event-stream");
            res.setCharacterEncoding("UTF-8");

            final String msg = req.getParameter("msg");

            // start async
            final AsyncContext ac = req.startAsync();

            final PrintWriter writer = res.getWriter();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    // echo msg 5 times
                    for (int i = 0; i < 5; i++) {
                        if (i == 4) { // last
                            // SSE event field
                            writer.write("event: close\n");
                        }
                        // SSE data field
                        // last field with blank new line
                        writer.write("data: " + msg + "\n\n");
                        writer.flush();

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException iex) {
                            iex.printStackTrace();
                        }
                    }

                    // complete async
                    ac.complete();
                }
            };

            executorService.submit(runnable);
        }
    }
}
