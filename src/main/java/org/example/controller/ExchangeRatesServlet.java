package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.example.dto.ExchangeRateDTO;
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
import java.util.Optional;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    ExchangeRateService exchangeRateService;

    @Override
    public void init() throws ServletException {
        exchangeRateService = new ExchangeRateService();

        super.init();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("txt/html");
        PrintWriter writer = resp.getWriter();
        ObjectMapper objectMapper = new ObjectMapper();
        List<ExchangeRate> result = exchangeRateService.getListOfExchangeRates();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        String jsonResult = objectMapper.writeValueAsString(result);
        writer.write(jsonResult);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String baseCode = req.getParameter("baseCurrencyCode");
        String targetCode = req.getParameter("targetCurrencyCode");
        String rate = req.getParameter("rate");
        resp.setContentType("txt/html");
        PrintWriter writer = resp.getWriter();
        ExchangeRateDTO exchangeRateDTO = new ExchangeRateDTO(baseCode, targetCode, new BigDecimal(rate));
        Optional<ExchangeRate> result = Optional.empty();
        try {
            result = exchangeRateService.putNewExchangeRate(exchangeRateDTO);
        } catch (SQLException e) {
            writer.write(e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        // writer.write(objectMapper.writeValueAsString(result.get()));

    }
}
