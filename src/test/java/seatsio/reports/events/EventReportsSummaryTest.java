package seatsio.reports.events;

import org.junit.jupiter.api.Test;
import seatsio.SeatsioClientTest;
import seatsio.events.Channel;
import seatsio.events.CreateEventParams;
import seatsio.events.Event;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static seatsio.events.EventObjectInfo.*;
import static seatsio.reports.events.EventReportSummaryItemBuilder.anEventReportSummaryItem;

public class EventReportsSummaryTest extends SeatsioClientTest {

    @Test
    public void summaryByStatus() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.book(event.key, List.of("A-1"));

        Map<String, EventReportSummaryItem> report = client.eventReports.summaryByStatus(event.key);

        EventReportSummaryItem bookedReport = anEventReportSummaryItem()
                .withCount(1)
                .withBySection(Map.of(NO_SECTION, 1))
                .withByCategoryKey(Map.of("9", 1))
                .withByCategoryLabel(Map.of("Cat1", 1))
                .withByAvailability(Map.of(NOT_AVAILABLE, 1))
                .withByAvailabilityReason(Map.of(BOOKED, 1))
                .withByChannel(Map.of(NO_CHANNEL, 1))
                .withByObjectType(Map.of("seat", 1))
                .withByZone(Map.of(NO_ZONE, 1))
                .build();
        EventReportSummaryItem freeReport = anEventReportSummaryItem()
                .withCount(231)
                .withBySection(Map.of(NO_SECTION, 231))
                .withByCategoryKey(Map.of("9", 115, "10", 116))
                .withByCategoryLabel(Map.of("Cat1", 115, "Cat2", 116))
                .withByAvailability(Map.of(AVAILABLE, 231))
                .withByAvailabilityReason(Map.of(AVAILABLE, 231))
                .withByChannel(Map.of(NO_CHANNEL, 231))
                .withByObjectType(Map.of("seat", 31, "generalAdmission", 200))
                .withByZone(Map.of(NO_ZONE, 231))
                .build();
        assertThat(report).isEqualTo(Map.of(BOOKED, bookedReport, FREE, freeReport));
    }

    @Test
    public void summaryByObjectType() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);

        Map<String, EventReportSummaryItem> report = client.eventReports.summaryByObjectType(event.key);

        EventReportSummaryItem seatReport = anEventReportSummaryItem()
                .withCount(32)
                .withBySection(Map.of(NO_SECTION, 32))
                .withByCategoryKey(Map.of("9", 16, "10", 16))
                .withByCategoryLabel(Map.of("Cat1", 16, "Cat2", 16))
                .withByAvailability(Map.of(AVAILABLE, 32))
                .withByAvailabilityReason(Map.of(AVAILABLE, 32))
                .withByChannel(Map.of(NO_CHANNEL, 32))
                .withByStatus(Map.of(FREE, 32))
                .withByZone(Map.of(NO_ZONE, 32))
                .build();
        EventReportSummaryItem generalAdmissionReport = anEventReportSummaryItem()
                .withCount(200)
                .withBySection(Map.of(NO_SECTION, 200))
                .withByCategoryKey(Map.of("9", 100, "10", 100))
                .withByCategoryLabel(Map.of("Cat1", 100, "Cat2", 100))
                .withByAvailability(Map.of(AVAILABLE, 200))
                .withByAvailabilityReason(Map.of(AVAILABLE, 200))
                .withByChannel(Map.of(NO_CHANNEL, 200))
                .withByStatus(Map.of(FREE, 200))
                .withByZone(Map.of(NO_ZONE, 200))
                .build();
        EventReportSummaryItem emptyReport = anEventReportSummaryItem()
                .withCount(0)
                .withBySection(Map.of())
                .withByCategoryKey(Map.of())
                .withByCategoryLabel(Map.of())
                .withByAvailability(Map.of())
                .withByAvailabilityReason(Map.of())
                .withByChannel(Map.of())
                .withByStatus(Map.of())
                .withByZone(Map.of())
                .build();
        assertThat(report).isEqualTo(Map.of(
                "seat", seatReport,
                "generalAdmission", generalAdmissionReport,
                "booth", emptyReport,
                "table", emptyReport
        ));
    }

    @Test
    public void summaryByCategoryKey() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.book(event.key, List.of("A-1"));

        Map<String, EventReportSummaryItem> report = client.eventReports.summaryByCategoryKey(event.key);

        EventReportSummaryItem cat9Report = anEventReportSummaryItem()
                .withCount(116)
                .withBySection(Map.of(NO_SECTION, 116))
                .withByStatus(Map.of(BOOKED, 1, FREE, 115))
                .withByAvailability(Map.of(AVAILABLE, 115, NOT_AVAILABLE, 1))
                .withByAvailabilityReason(Map.of(AVAILABLE, 115, BOOKED, 1))
                .withByChannel(Map.of(NO_CHANNEL, 116))
                .withByObjectType(Map.of("seat", 16, "generalAdmission", 100))
                .withByZone(Map.of(NO_ZONE, 116))
                .build();
        EventReportSummaryItem cat10Report = anEventReportSummaryItem()
                .withCount(116)
                .withBySection(Map.of(NO_SECTION, 116))
                .withByStatus(Map.of(FREE, 116))
                .withByAvailability(Map.of(AVAILABLE, 116))
                .withByAvailabilityReason(Map.of(AVAILABLE, 116))
                .withByChannel(Map.of(NO_CHANNEL, 116))
                .withByObjectType(Map.of("seat", 16, "generalAdmission", 100))
                .withByZone(Map.of(NO_ZONE, 116))
                .build();
        EventReportSummaryItem cat11Report = anEventReportSummaryItem()
                .withCount(0)
                .withBySection(Map.of())
                .withByStatus(Map.of())
                .withByAvailability(Map.of())
                .withByAvailabilityReason(Map.of())
                .withByChannel(Map.of())
                .withByObjectType(Map.of())
                .withByZone(Map.of())
                .build();
        EventReportSummaryItem noCategoryReport = anEventReportSummaryItem()
                .withCount(0)
                .withBySection(Map.of())
                .withByStatus(Map.of())
                .withByAvailability(Map.of())
                .withByAvailabilityReason(Map.of())
                .withByChannel(Map.of())
                .withByObjectType(Map.of())
                .withByZone(Map.of())
                .build();
        assertThat(report).isEqualTo(Map.of(
                "9", cat9Report,
                "10", cat10Report,
                "string11", cat11Report,
                "NO_CATEGORY", noCategoryReport));
    }

    @Test
    public void summaryByCategoryLabel() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.book(event.key, List.of("A-1"));

        Map<String, EventReportSummaryItem> report = client.eventReports.summaryByCategoryLabel(event.key);

        EventReportSummaryItem cat1Report = anEventReportSummaryItem()
                .withCount(116)
                .withBySection(Map.of(NO_SECTION, 116))
                .withByStatus(Map.of(BOOKED, 1, FREE, 115))
                .withByAvailability(Map.of(AVAILABLE, 115, NOT_AVAILABLE, 1))
                .withByAvailabilityReason(Map.of(AVAILABLE, 115, BOOKED, 1))
                .withByChannel(Map.of(NO_CHANNEL, 116))
                .withByObjectType(Map.of("seat", 16, "generalAdmission", 100))
                .withByZone(Map.of(NO_ZONE, 116))
                .build();
        EventReportSummaryItem cat2Report = anEventReportSummaryItem()
                .withCount(116)
                .withBySection(Map.of(NO_SECTION, 116))
                .withByStatus(Map.of(FREE, 116))
                .withByAvailability(Map.of(AVAILABLE, 116))
                .withByAvailabilityReason(Map.of(AVAILABLE, 116))
                .withByChannel(Map.of(NO_CHANNEL, 116))
                .withByObjectType(Map.of("seat", 16, "generalAdmission", 100))
                .withByZone(Map.of(NO_ZONE, 116))
                .build();
        EventReportSummaryItem noCategoryReport = anEventReportSummaryItem()
                .withCount(0)
                .withBySection(Map.of())
                .withByStatus(Map.of())
                .withByAvailability(Map.of())
                .withByAvailabilityReason(Map.of())
                .withByChannel(Map.of())
                .withByObjectType(Map.of())
                .withByZone(Map.of())
                .build();
        EventReportSummaryItem cat3Report = anEventReportSummaryItem()
                .withCount(0)
                .withBySection(Map.of())
                .withByStatus(Map.of())
                .withByAvailability(Map.of())
                .withByAvailabilityReason(Map.of())
                .withByChannel(Map.of())
                .withByObjectType(Map.of())
                .withByZone(Map.of())
                .build();
        assertThat(report).isEqualTo(Map.of(
                "Cat1", cat1Report,
                "Cat2", cat2Report,
                "Cat3", cat3Report,
                "NO_CATEGORY", noCategoryReport));
    }

    @Test
    public void summaryBySection() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.book(event.key, List.of("A-1"));

        Map<String, EventReportSummaryItem> report = client.eventReports.summaryBySection(event.key);

        EventReportSummaryItem noSectionReport = anEventReportSummaryItem()
                .withCount(232)
                .withByStatus(Map.of(BOOKED, 1, FREE, 231))
                .withByCategoryKey(Map.of("9", 116, "10", 116))
                .withByCategoryLabel(Map.of("Cat1", 116, "Cat2", 116))
                .withByAvailability(Map.of(AVAILABLE, 231, NOT_AVAILABLE, 1))
                .withByAvailabilityReason(Map.of(AVAILABLE, 231, BOOKED, 1))
                .withByChannel(Map.of(NO_CHANNEL, 232))
                .withByObjectType(Map.of("seat", 32, "generalAdmission", 200))
                .withByZone(Map.of(NO_ZONE, 232))
                .build();
        assertThat(report).isEqualTo(Map.of(NO_SECTION, noSectionReport));
    }

    @Test
    public void summaryByZone() {
        String chartKey = createTestChartWithZones();
        Event event = client.events.create(chartKey);

        Map<String, EventReportSummaryItem> report = client.eventReports.summaryByZone(event.key);

        EventReportSummaryItem reportForZoneFinishline = anEventReportSummaryItem()
                .withCount(2865)
                .withByCategoryKey(Map.of("1", 2865))
                .withByCategoryLabel(Map.of("Goal Stands", 2865))
                .withByObjectType(Map.of("seat", 2865))
                .withBySection(Map.of("Goal Stand 3", 2215, "Goal Stand 4", 650))
                .withByStatus(Map.of(FREE, 2865))
                .withByAvailability(Map.of(AVAILABLE, 2865))
                .withByAvailabilityReason(Map.of(AVAILABLE, 2865))
                .withByChannel(Map.of(NO_CHANNEL, 2865))
                .build();
        EventReportSummaryItem reportForZoneMidtrack = anEventReportSummaryItem()
                .withCount(6032)
                .withByCategoryKey(Map.of("2", 6032))
                .withByCategoryLabel(Map.of("Mid Track Stand", 6032))
                .withByObjectType(Map.of("seat", 6032))
                .withBySection(Map.of("MT1", 2418, "MT3", 3614))
                .withByStatus(Map.of(FREE, 6032))
                .withByAvailability(Map.of(AVAILABLE, 6032))
                .withByAvailabilityReason(Map.of(AVAILABLE, 6032))
                .withByChannel(Map.of(NO_CHANNEL, 6032))
                .build();
        EventReportSummaryItem reportForNoZone = anEventReportSummaryItem()
                .withCount(0)
                .withByCategoryKey(Map.of())
                .withByCategoryLabel(Map.of())
                .withByObjectType(Map.of())
                .withBySection(Map.of())
                .withByStatus(Map.of())
                .withByAvailability(Map.of())
                .withByAvailabilityReason(Map.of())
                .withByChannel(Map.of())
                .build();
        assertThat(report).isEqualTo(Map.of(
                "finishline", reportForZoneFinishline,
                "midtrack", reportForZoneMidtrack,
                NO_ZONE, reportForNoZone));
    }

    @Test
    public void summaryByAvailability() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.book(event.key, List.of("A-1"));

        Map<String, EventReportSummaryItem> report = client.eventReports.summaryByAvailability(event.key);

        EventReportSummaryItem availableReport = anEventReportSummaryItem()
                .withCount(231)
                .withBySection(Map.of(NO_SECTION, 231))
                .withByStatus(Map.of(FREE, 231))
                .withByCategoryKey(Map.of("9", 115, "10", 116))
                .withByCategoryLabel(Map.of("Cat1", 115, "Cat2", 116))
                .withByChannel(Map.of(NO_CHANNEL, 231))
                .withByAvailabilityReason(Map.of(AVAILABLE, 231))
                .withByObjectType(Map.of("seat", 31, "generalAdmission", 200))
                .withByZone(Map.of(NO_ZONE, 231))
                .build();
        EventReportSummaryItem notAvailableReport = anEventReportSummaryItem()
                .withCount(1)
                .withBySection(Map.of(NO_SECTION, 1))
                .withByStatus(Map.of(BOOKED, 1))
                .withByCategoryKey(Map.of("9", 1))
                .withByCategoryLabel(Map.of("Cat1", 1))
                .withByChannel(Map.of(NO_CHANNEL, 1))
                .withByAvailabilityReason(Map.of(BOOKED, 1))
                .withByObjectType(Map.of("seat", 1))
                .withByZone(Map.of(NO_ZONE, 1))
                .build();
        assertThat(report).isEqualTo(Map.of(AVAILABLE, availableReport, NOT_AVAILABLE, notAvailableReport));
    }

    @Test
    public void summaryByAvailabilityReason() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey);
        client.events.book(event.key, List.of("A-1"));

        Map<String, EventReportSummaryItem> report = client.eventReports.summaryByAvailabilityReason(event.key);

        EventReportSummaryItem availableReport = anEventReportSummaryItem()
                .withCount(231)
                .withBySection(Map.of(NO_SECTION, 231))
                .withByStatus(Map.of(FREE, 231))
                .withByCategoryKey(Map.of("9", 115, "10", 116))
                .withByCategoryLabel(Map.of("Cat1", 115, "Cat2", 116))
                .withByChannel(Map.of(NO_CHANNEL, 231))
                .withByAvailability(Map.of(AVAILABLE, 231))
                .withByObjectType(Map.of("seat", 31, "generalAdmission", 200))
                .withByZone(Map.of(NO_ZONE, 231))
                .build();
        EventReportSummaryItem bookedReport = anEventReportSummaryItem()
                .withCount(1)
                .withBySection(Map.of(NO_SECTION, 1))
                .withByStatus(Map.of(BOOKED, 1))
                .withByCategoryKey(Map.of("9", 1))
                .withByCategoryLabel(Map.of("Cat1", 1))
                .withByChannel(Map.of(NO_CHANNEL, 1))
                .withByAvailability(Map.of(NOT_AVAILABLE, 1))
                .withByObjectType(Map.of("seat", 1))
                .withByZone(Map.of(NO_ZONE, 1))
                .build();
        EventReportSummaryItem emptyReport = anEventReportSummaryItem()
                .withBySection(Map.of())
                .withByStatus(Map.of())
                .withByCategoryKey(Map.of())
                .withByCategoryLabel(Map.of())
                .withByChannel(Map.of())
                .withByAvailability(Map.of())
                .withByObjectType(Map.of())
                .withByZone(Map.of())
                .build();
        assertThat(report).isEqualTo(Map.of(
                AVAILABLE, availableReport,
                BOOKED, bookedReport,
                HELD, emptyReport,
                DISABLED_BY_SOCIAL_DISTANCING, emptyReport,
                NOT_FOR_SALE, emptyReport
        ));
    }

    @Test
    public void summaryByChannel() {
        String chartKey = createTestChart();
        Event event = client.events.create(chartKey, new CreateEventParams().withChannels(List.of(
                new Channel("channel1", "channel 1", "#FFFF99", 1, Set.of("A-1", "A-2"))
        )));

        Map<String, EventReportSummaryItem> report = client.eventReports.summaryByChannel(event.key);

        EventReportSummaryItem channel1Report = anEventReportSummaryItem()
                .withCount(2)
                .withBySection(Map.of(NO_SECTION, 2))
                .withByCategoryKey(Map.of("9", 2))
                .withByCategoryLabel(Map.of("Cat1", 2))
                .withByAvailability(Map.of(AVAILABLE, 2))
                .withByAvailabilityReason(Map.of(AVAILABLE, 2))
                .withByStatus(Map.of(FREE, 2))
                .withByObjectType(Map.of("seat", 2))
                .withByZone(Map.of(NO_ZONE, 2))
                .build();
        EventReportSummaryItem noChannelReport = anEventReportSummaryItem()
                .withCount(230)
                .withBySection(Map.of(NO_SECTION, 230))
                .withByCategoryKey(Map.of("9", 114, "10", 116))
                .withByCategoryLabel(Map.of("Cat1", 114, "Cat2", 116))
                .withByAvailability(Map.of(AVAILABLE, 230))
                .withByAvailabilityReason(Map.of(AVAILABLE, 230))
                .withByStatus(Map.of(FREE, 230))
                .withByObjectType(Map.of("seat", 30, "generalAdmission", 200))
                .withByZone(Map.of(NO_ZONE, 230))
                .build();
        assertThat(report).isEqualTo(Map.of("channel1", channel1Report, NO_CHANNEL, noChannelReport));
    }

}
