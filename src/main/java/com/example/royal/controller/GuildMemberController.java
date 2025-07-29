package com.example.royal.controller;

import com.example.royal.dto.GuildMemberDto;
import com.example.royal.service.DiscordMessageService;
import com.example.royal.service.GuildMemberService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/guild")
@RequiredArgsConstructor
public class GuildMemberController {

    private final GuildMemberService service;

    // 전체 길드 멤버 조회
    @GetMapping
    public String list(Model model,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(required = false) String keyword) {  // 0-based page index
        Pageable pageable = PageRequest.of(page, 10); // 한 페이지 10명
        Page<GuildMemberDto> memberPage = service.getAll(pageable);

        if (keyword != null && !keyword.isBlank()) {
            memberPage = service.searchByKeyword(keyword, pageable);
        } else {
            memberPage = service.getAll(pageable);
        }

        model.addAttribute("memberPage", memberPage);
        model.addAttribute("members", memberPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", memberPage.getTotalPages());

        return "guild/member-list";
    }

    // 길드 멤버 생성 폼 페이지 요청
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("member", new GuildMemberDto());
        return "guild/member-create";
    }

    // 길드 멤버 생성
    @PostMapping
    public String create(@ModelAttribute GuildMemberDto dto) {
        service.save(dto);
        return "redirect:/guild";
    }

    // 길드 멤버 수정 폼 페이지
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("member", service.getById(id));
        return "guild/member-edit";
    }

    // 길드 멤버 수정
    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id, @ModelAttribute GuildMemberDto dto) {
        service.update(id, dto);
        return "redirect:/guild";
    }

    // 길드 멤버 삭제
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/guild";
    }


    // ===== 디스코드 메시지 분류 기능 =====


    // 디스코드 메시지 입력 폼 페이지
    @GetMapping("/discord-messages")
    public String discordMessagesForm() {
        return "guild/discord-messages";
    }

    // 디스코드 메시지를 사용자별로 분류하여 표시
    @PostMapping("/discord-messages")
    public String showDiscordMessages(@RequestParam("discordText") String discordText, Model model) {
        try {
            Map<String, List<String>> userMessages = DiscordMessageService.parseDiscordMessagesByUser(discordText);
            model.addAttribute("userMessages", userMessages);
            model.addAttribute("totalUsers", userMessages.size());
            model.addAttribute("discordText", discordText);
            return "guild/discord-messages-result";
        } catch (Exception e) {
            model.addAttribute("error", "메시지 파싱 중 오류가 발생했습니다: " + e.getMessage());
            return "guild/discord-messages";
        }
    }


    // ===== 엑셀 다운로드 (향상된 디자인) =====
    @GetMapping("/export")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        String fileName = "길드원_목록_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".xlsx";
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));

        List<GuildMemberDto> members = service.getAllMembers();

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("길드원 목록");

        // ===== 스타일 정의 =====
        XSSFFont mainTitleFont = workbook.createFont();
        mainTitleFont.setBold(true);
        mainTitleFont.setFontHeightInPoints((short) 20);
        mainTitleFont.setFontName("맑은 고딕");
        mainTitleFont.setColor(IndexedColors.WHITE.getIndex());

        CellStyle mainTitleStyle = workbook.createCellStyle();
        mainTitleStyle.setFont(mainTitleFont);
        mainTitleStyle.setAlignment(HorizontalAlignment.CENTER);
        mainTitleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        mainTitleStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        mainTitleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFFont subTitleFont = workbook.createFont();
        subTitleFont.setBold(true);
        subTitleFont.setFontHeightInPoints((short) 12);
        subTitleFont.setFontName("맑은 고딕");

        CellStyle subTitleStyle = workbook.createCellStyle();
        subTitleStyle.setFont(subTitleFont);
        subTitleStyle.setAlignment(HorizontalAlignment.RIGHT);
        subTitleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        subTitleStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        subTitleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFFont headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 13);
        headerFont.setFontName("맑은 고딕");
        headerFont.setColor(IndexedColors.WHITE.getIndex());

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle dataStyle = workbook.createCellStyle();
        XSSFFont dataFont = workbook.createFont();
        dataFont.setFontName("맑은 고딕");
        dataFont.setFontHeightInPoints((short) 11);
        dataStyle.setFont(dataFont);
        dataStyle.setWrapText(true);
        dataStyle.setVerticalAlignment(VerticalAlignment.TOP);
        dataStyle.setAlignment(HorizontalAlignment.LEFT);

        CellStyle altDataStyle = workbook.createCellStyle();
        altDataStyle.cloneStyleFrom(dataStyle);
        altDataStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        altDataStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle summaryStyle = workbook.createCellStyle();
        XSSFFont summaryFont = workbook.createFont();
        summaryFont.setBold(true);
        summaryFont.setFontName("맑은 고딕");
        summaryFont.setFontHeightInPoints((short) 14);
        summaryFont.setColor(IndexedColors.WHITE.getIndex());
        summaryStyle.setFont(summaryFont);
        summaryStyle.setAlignment(HorizontalAlignment.CENTER);
        summaryStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        summaryStyle.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
        summaryStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // ===== 제목 =====
        Row titleRow = sheet.createRow(0);
        titleRow.setHeightInPoints(40);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("GUILD MEMBERS MANAGEMENT");
        titleCell.setCellStyle(mainTitleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));

        Row subTitleRow = sheet.createRow(1);
        subTitleRow.setHeightInPoints(25);
        Cell subTitleCell = subTitleRow.createCell(0);
        subTitleCell.setCellValue("길드원 현황 리포트");
        subTitleCell.setCellStyle(subTitleStyle);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 1));

        Cell dateCell = subTitleRow.createCell(2);
        dateCell.setCellValue("다운로드: " + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")));
        dateCell.setCellStyle(subTitleStyle);

        sheet.createRow(2); // 빈 행

        // ===== 헤더 =====
        Row headerRow = sheet.createRow(3);
        headerRow.setHeightInPoints(30);
        String[] headers = {"기사/본캐", "부기사/부캐", "배럭"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // ===== 데이터 =====
        for (int i = 0; i < members.size(); i++) {
            GuildMemberDto member = members.get(i);
            Row row = sheet.createRow(i + 4);

            int lineCount = Math.max(1,
                    Math.max(member.getSubCharacterNames().size(), member.getKnightCharacterNames().size()));
            row.setHeightInPoints(20 * lineCount);

            boolean isEven = (i % 2 == 0);
            CellStyle currentStyle = isEven ? dataStyle : altDataStyle;

            Cell mainCell = row.createCell(0);
            mainCell.setCellValue(member.getName() != null ? member.getName() : "미등록");
            mainCell.setCellStyle(currentStyle);

            Cell subCell = row.createCell(1);
            String subChars = member.getSubCharacterNames().isEmpty() ? "없음"
                    : String.join("\n", member.getSubCharacterNames());
            subCell.setCellValue(subChars);
            subCell.setCellStyle(currentStyle);

            Cell knightCell = row.createCell(2);
            String knightChars = member.getKnightCharacterNames().isEmpty() ? "없음"
                    : String.join("\n", member.getKnightCharacterNames());
            knightCell.setCellValue(knightChars);
            knightCell.setCellStyle(currentStyle);
        }

        // ===== 통계 =====
        int statsRowIdx = members.size() + 6;
        Row totalRow = sheet.createRow(statsRowIdx);
        Cell totalCell = totalRow.createCell(0);
        totalCell.setCellValue("총 길드원: " + members.size() + "명");
        totalCell.setCellStyle(summaryStyle);
        sheet.addMergedRegion(new CellRangeAddress(statsRowIdx, statsRowIdx, 0, 2));

        long subCount = members.stream().filter(m -> !m.getSubCharacterNames().isEmpty()).count();
        Row subRow = sheet.createRow(statsRowIdx + 1);
        Cell subCell = subRow.createCell(0);
        subCell.setCellValue("부캐 보유자: " + subCount + "명");

        CellStyle subSummaryStyle = workbook.createCellStyle();
        subSummaryStyle.cloneStyleFrom(summaryStyle);
        subSummaryStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        subSummaryStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        subCell.setCellStyle(subSummaryStyle);
        sheet.addMergedRegion(new CellRangeAddress(statsRowIdx + 1, statsRowIdx + 1, 0, 2));

        // ===== 열 너비 및 기타 설정 =====
        sheet.setColumnWidth(0, 5000);
        sheet.setColumnWidth(1, 7000);
        sheet.setColumnWidth(2, 7000);

        sheet.createFreezePane(0, 4);
        sheet.setAutoFilter(new CellRangeAddress(3, members.size() + 3, 0, 2));

        ServletOutputStream out = response.getOutputStream();
        workbook.write(out);
        out.flush();  // 반드시 flush
        out.close();  // 반드시 close
        workbook.close();
    }




}
