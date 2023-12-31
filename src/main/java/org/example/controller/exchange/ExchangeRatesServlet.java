package org.example.controller.exchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.example.dto.ExchangeRateDTO;
import org.example.exceptions.UniqueConstraintException;
import org.example.model.ExchangeRate;
import org.example.service.ExchangeRateService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private ExchangeRateService exchangeRateService;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        exchangeRateService = new ExchangeRateService();
        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // Enable pretty-printing
        super.init();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter writer = resp.getWriter();
        List<ExchangeRate> result = exchangeRateService.getListOfExchangeRates();
        String jsonResult = objectMapper.writeValueAsString(result);
        writer.write(jsonResult);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCode = req.getParameter("baseCurrencyCode");
        String targetCode = req.getParameter("targetCurrencyCode");
        String rate = req.getParameter("rate");
        PrintWriter writer = resp.getWriter();
        if (baseCode == null || targetCode == null || rate == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writer.write("Miss one or more parameters");
            return;
        }
        ExchangeRateDTO exchangeRateDTO = new ExchangeRateDTO(baseCode, targetCode, new BigDecimal(rate));
        try {
            writer.write(objectMapper.writeValueAsString(exchangeRateService.putNewExchangeRate(exchangeRateDTO).get()));
        } catch (UniqueConstraintException e) {
            writer.write(e.getMessage());
            resp.setStatus(409);
        } catch (SQLException e) {
            writer.write(e.getMessage());
            resp.setStatus(500);
        }
    }
}
