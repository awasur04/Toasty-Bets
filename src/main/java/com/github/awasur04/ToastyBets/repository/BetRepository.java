package com.github.awasur04.ToastyBets.repository;

import com.github.awasur04.ToastyBets.models.Bet;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BetRepository extends CrudRepository<Bet, Integer> {
    @Query(value = "SELECT * FROM bets WHERE bet_status = 'ACTIVE'", nativeQuery = true)
    List<Bet> findActiveBets();

    @Query(value = "SELECT * FROM bets WHERE week_number = :weekNumber AND discord_id = :discordId", nativeQuery = true)
    List<Bet> findUserCurrentWeekBets(@Param("weekNumber") int weekNumber, @Param("discordId") String discordId);
}
