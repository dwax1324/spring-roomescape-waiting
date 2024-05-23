package roomescape.reservation.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.system.auth.annotation.Admin;
import roomescape.system.auth.annotation.MemberId;
import roomescape.system.dto.response.ApiResponse;
import roomescape.reservation.dto.request.FilteredReservationRequest;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.reservation.dto.response.MemberReservationsResponse;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.dto.response.ReservationTimeInfosResponse;
import roomescape.reservation.dto.response.ReservationsResponse;
import roomescape.reservation.service.ReservationService;

@RestController
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(final ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Admin
    @GetMapping("/reservations")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ReservationsResponse> getAllReservations() {
        return ApiResponse.success(reservationService.findAllReservations());
    }

    @GetMapping("/reservations-mine")
    public ApiResponse<MemberReservationsResponse> getMemberReservations(@MemberId final Long memberId) {
        return ApiResponse.success(reservationService.findReservationByMemberId(memberId));
    }

    @GetMapping("/reservations/themes/{themeId}/times")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ReservationTimeInfosResponse> getReservationTimeInfos(
            @NotNull(message = "themeId는 null 또는 공백일 수 없습니다.") @PathVariable final Long themeId,
            @NotNull(message = "날짜는 null일 수 없습니다.") @RequestParam final LocalDate date) {
        return ApiResponse.success(reservationService.findReservationsByDateAndThemeId(date, themeId));
    }

    @Admin
    @GetMapping("/reservations/search")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ReservationsResponse> getReservationBySearching(
            @RequestParam(required = false) final Long themeId,
            @RequestParam(required = false) final Long memberId,
            @RequestParam(required = false) final LocalDate dateFrom,
            @RequestParam(required = false) final LocalDate dateTo
    ) {
        return ApiResponse.success(
                reservationService.findFilteredReservations(
                        new FilteredReservationRequest(themeId, memberId, dateFrom, dateTo)
                )
        );
    }

    @PostMapping("/reservations")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ReservationResponse> saveReservation(
            @Valid @RequestBody final ReservationRequest reservationRequest,
            @MemberId final Long memberId,
            final HttpServletResponse response
    ) {
        final ReservationResponse reservationResponse = reservationService.addReservation(reservationRequest, memberId);

        response.setHeader(HttpHeaders.LOCATION, "/reservations/" + reservationResponse.id());
        return ApiResponse.success(reservationResponse);
    }

    @DeleteMapping("/reservations/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> removeReservation(
            @MemberId final Long memberId,
            @NotNull(message = "reservationId는 null 또는 공백일 수 없습니다.") @PathVariable("id") final Long reservationId
    ) {
        reservationService.removeReservationById(reservationId, memberId);

        return ApiResponse.success();
    }
}
