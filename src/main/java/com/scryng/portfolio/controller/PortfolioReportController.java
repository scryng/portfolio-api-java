package com.scryng.portfolio.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scryng.portfolio.dto.response.PortfolioReportResponse;
import com.scryng.portfolio.service.PortfolioReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/portfolio")
@RequiredArgsConstructor
public class PortfolioReportController {

	private final PortfolioReportService portfolioReportService;

	@GetMapping("/report")
	public PortfolioReportResponse report() {
		return portfolioReportService.generate();
	}

}
