package org.example.controller.currency;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.example.exceptions.UniqueConstraintException;
import org.example.model.Currency;
import org.example.service.CurrenciesService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private CurrenciesService currenciesService;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        currenciesService = new CurrenciesService();
        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // Enable pretty-printing
        super.init();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter printWriter = resp.getWriter();
        List<Currency> currencies = currenciesService.getCurrencies();
        if (currencies.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            printWriter.write("Nothing was Found");
        } else {
            String cursString = objectMapper.writeValueAsString(currencies);
            resp.setStatus(HttpServletResponse.SC_OK);
            printWriter.write(cursString);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("name");
        String code = req.getParameter("code");
        String sign = req.getParameter("sign");
        BufferedReader reader = req.getReader();
        PrintWriter writer = resp.getWriter();
        if ( name == null || code == null || sign == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writer.write("Miss one or more parameters");
            return;
        }
        Currency currencyToPut = new Currency(0, code, name, sign);
        try {
            Currency currency = currenciesService.setNewCurrency(currencyToPut);
            writer.write(objectMapper.writeValueAsString(currency));
        } catch (UniqueConstraintException e) {
            resp.setStatus(409);
            writer.write(e.getMessage());
        } catch (Exception e) {
            writer.write("Database Exception");
            resp.setStatus(500);
        }


    }
}
