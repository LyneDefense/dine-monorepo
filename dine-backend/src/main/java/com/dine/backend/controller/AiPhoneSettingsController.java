package com.dine.backend.controller;

import com.dine.backend.common.Result;
import com.dine.backend.dto.request.AiPhoneSettingsRequest;
import com.dine.backend.dto.request.AiPhoneActiveHoursRequest;
import com.dine.backend.dto.request.AiPhoneFaqRequest;
import com.dine.backend.dto.request.AiPhoneInstructionRequest;
import com.dine.backend.dto.response.AiPhoneSettingsVO;
import com.dine.backend.dto.response.AiPhoneActiveHoursVO;
import com.dine.backend.dto.response.AiPhoneFaqVO;
import com.dine.backend.dto.response.AiPhoneInstructionVO;
import com.dine.backend.service.AiPhoneSettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "AI Phone Agent", description = "AI电话代理设置接口")
@RestController
@RequestMapping("/api/restaurants/{restaurantId}/ai-phone")
@RequiredArgsConstructor
public class AiPhoneSettingsController {

    private final AiPhoneSettingsService aiPhoneSettingsService;

    // ==================== AI Phone Settings ====================

    @Operation(summary = "获取AI电话设置")
    @GetMapping("/settings")
    public Result<AiPhoneSettingsVO> getSettings(@PathVariable Long restaurantId) {
        return Result.success(aiPhoneSettingsService.getSettings(restaurantId));
    }

    @Operation(summary = "更新AI电话设置")
    @PutMapping("/settings")
    public Result<AiPhoneSettingsVO> updateSettings(
            @PathVariable Long restaurantId,
            @Valid @RequestBody AiPhoneSettingsRequest request) {
        return Result.success(aiPhoneSettingsService.updateSettings(restaurantId, request));
    }

    // ==================== Active Hours ====================

    @Operation(summary = "获取AI服务时间")
    @GetMapping("/active-hours")
    public Result<List<AiPhoneActiveHoursVO>> getActiveHours(@PathVariable Long restaurantId) {
        return Result.success(aiPhoneSettingsService.getActiveHours(restaurantId));
    }

    @Operation(summary = "添加AI服务时间")
    @PostMapping("/active-hours")
    public Result<AiPhoneActiveHoursVO> addActiveHours(
            @PathVariable Long restaurantId,
            @Valid @RequestBody AiPhoneActiveHoursRequest request) {
        return Result.success(aiPhoneSettingsService.addActiveHours(restaurantId, request));
    }

    @Operation(summary = "删除AI服务时间")
    @DeleteMapping("/active-hours/{id}")
    public Result<Void> deleteActiveHours(@PathVariable Long restaurantId, @PathVariable Long id) {
        aiPhoneSettingsService.deleteActiveHours(restaurantId, id);
        return Result.success();
    }

    // ==================== FAQs ====================

    @Operation(summary = "获取常见问题列表")
    @GetMapping("/faqs")
    public Result<List<AiPhoneFaqVO>> getFaqs(@PathVariable Long restaurantId) {
        return Result.success(aiPhoneSettingsService.getFaqs(restaurantId));
    }

    @Operation(summary = "创建常见问题")
    @PostMapping("/faqs")
    public Result<AiPhoneFaqVO> createFaq(
            @PathVariable Long restaurantId,
            @Valid @RequestBody AiPhoneFaqRequest request) {
        return Result.success(aiPhoneSettingsService.addFaq(restaurantId, request));
    }

    @Operation(summary = "更新常见问题")
    @PutMapping("/faqs/{id}")
    public Result<AiPhoneFaqVO> updateFaq(
            @PathVariable Long restaurantId,
            @PathVariable Long id,
            @Valid @RequestBody AiPhoneFaqRequest request) {
        return Result.success(aiPhoneSettingsService.updateFaq(restaurantId, id, request));
    }

    @Operation(summary = "删除常见问题")
    @DeleteMapping("/faqs/{id}")
    public Result<Void> deleteFaq(@PathVariable Long restaurantId, @PathVariable Long id) {
        aiPhoneSettingsService.deleteFaq(restaurantId, id);
        return Result.success();
    }

    // ==================== Instructions ====================

    @Operation(summary = "获取AI指令列表")
    @GetMapping("/instructions")
    public Result<List<AiPhoneInstructionVO>> getInstructions(@PathVariable Long restaurantId) {
        return Result.success(aiPhoneSettingsService.getInstructions(restaurantId));
    }

    @Operation(summary = "创建AI指令")
    @PostMapping("/instructions")
    public Result<AiPhoneInstructionVO> createInstruction(
            @PathVariable Long restaurantId,
            @Valid @RequestBody AiPhoneInstructionRequest request) {
        return Result.success(aiPhoneSettingsService.addInstruction(restaurantId, request));
    }

    @Operation(summary = "更新AI指令")
    @PutMapping("/instructions/{id}")
    public Result<AiPhoneInstructionVO> updateInstruction(
            @PathVariable Long restaurantId,
            @PathVariable Long id,
            @Valid @RequestBody AiPhoneInstructionRequest request) {
        return Result.success(aiPhoneSettingsService.updateInstruction(restaurantId, id, request));
    }

    @Operation(summary = "删除AI指令")
    @DeleteMapping("/instructions/{id}")
    public Result<Void> deleteInstruction(@PathVariable Long restaurantId, @PathVariable Long id) {
        aiPhoneSettingsService.deleteInstruction(restaurantId, id);
        return Result.success();
    }
}
