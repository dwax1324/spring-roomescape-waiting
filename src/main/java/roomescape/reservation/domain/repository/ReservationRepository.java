package roomescape.reservation.domain.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import roomescape.member.domain.Member;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationTime;
import roomescape.theme.domain.Theme;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, JpaSpecificationExecutor<Reservation> {

    List<Reservation> findByReservationTime(ReservationTime reservationTime);

    List<Reservation> findByReservationTimeAndDateAndTheme(
            ReservationTime reservationTime,
            LocalDate date,
            Theme theme
    );

    List<Reservation> findByDateAndTheme(LocalDate date, Theme theme);

    List<Reservation> findByMember(Member member);
}
