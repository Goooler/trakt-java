package com.uwetrottmann.trakt.v2.entities;

import org.joda.time.DateTime;

import java.util.List;

public class SyncSeason {

    public Integer number;
    public List<SyncEpisode> episodes;

    public DateTime collected_at;
    public DateTime watched_at;

    public SyncSeason number(int number) {
        this.number = number;
        return this;
    }

    public SyncSeason collectedAt(DateTime collectedAt) {
        this.collected_at = collectedAt;
        return this;
    }

    public SyncSeason watchedAt(DateTime watchedAt) {
        this.watched_at = watchedAt;
        return this;
    }

}
