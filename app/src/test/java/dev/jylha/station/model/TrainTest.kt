package dev.jylha.station.model

import com.google.common.truth.Truth.assertThat
import kotlinx.datetime.Instant
import org.junit.Test

class TrainTest {

    private val scheduledTime1 = Instant.parse("2020-09-05T10:00:00Z")
    private val actualTime1 = Instant.parse("2020-09-05T10:02:00Z")
    private val scheduledTime2 = Instant.parse("2020-09-05T10:30:00Z")
    private val actualTime2 = Instant.parse("2020-09-05T10:31:00Z")
    private val scheduledTime3 = Instant.parse("2020-09-05T10:40:00Z")
    private val scheduledTime4 = Instant.parse("2020-09-05T11:10:00Z")

    private val train = Train(
        1, "S", Train.Category.LongDistance, timetable = listOf(
            departure(
                100, "5", scheduledTime1, actualTime = actualTime1,
                differenceInMinutes = 2, markedReady = true
            ),
            arrival(
                200, "1", scheduledTime2, actualTime = actualTime2,
                differenceInMinutes = 1
            ),
            departure(200, "1", scheduledTime3),
            arrival(300, "3", scheduledTime4)
        )
    )

    private val trainWithEmptyTimetable = Train(
        2, "IC", Train.Category.Commuter, "A", false, timetable = emptyList()
    )

    private val readyTrain = trainWithEmptyTimetable.copy(
        timetable = listOf(
            departure(1, "1", scheduledTime1, markedReady = true),
            arrival(2, "1", scheduledTime2)
        )
    )

    private val notReadyTrain = trainWithEmptyTimetable.copy(
        timetable = listOf(
            departure(1, "1", scheduledTime1),
            arrival(2, "1", scheduledTime2)
        )
    )

    private val trainWithSameEndpoints = trainWithEmptyTimetable.copy(
        timetable = listOf(
            departure(3, "4", scheduledTime1),
            arrival(3, "2", scheduledTime2)
        )
    )

    @Test fun `origin() returns the station code of the first timetable row`() {
        val result = train.origin()
        assertThat(result).isEqualTo(100)
    }

    @Test fun `origin() returns null for a train with empty timetable`() {
        val result = trainWithEmptyTimetable.origin()
        assertThat(result).isNull()
    }

    @Test fun `destination() returns the station code of the last timetable row`() {
        val result = train.destination()
        assertThat(result).isEqualTo(300)
    }

    @Test fun `destination() returns null for a train with empty timetable`() {
        val result = trainWithEmptyTimetable.destination()
        assertThat(result).isNull()
    }

    @Test fun `track() returns the track number for the given station`() {
        val result = train.track(200)
        assertThat(result).isEqualTo("1")
    }

    @Test fun `track() returns null for a station not in the timetable`() {
        val result = train.track(5)
        assertThat(result).isNull()
    }

    @Test fun `isReady() returns true when train is marked ready on origin`() {
        val result = readyTrain.isReady()
        assertThat(result).isTrue()
    }

    @Test fun `isReady() returns false when train is not marked ready on origin`() {
        val result = notReadyTrain.isReady()
        assertThat(result).isFalse()
    }

    @Test fun `isNotReady() returns false when train is marked ready on origin`() {
        val result = readyTrain.isNotReady()
        assertThat(result).isFalse()
    }

    @Test fun `hasReachedDestination() returns false when actualTime for destination is not set`() {
        val train = trainWithEmptyTimetable.copy(
            timetable = listOf(
                departure(1, "1", scheduledTime1, actualTime = actualTime1),
                arrival(2, "2", scheduledTime2, actualTime = null)
            )
        )
        val result = train.hasReachedDestination()
        assertThat(result).isFalse()
    }

    @Test fun `hasReachedDestination() returns true when actualTime for destination is set`() {
        val train = trainWithEmptyTimetable.copy(
            timetable = listOf(
                departure(1, "1", scheduledTime1, actualTime = actualTime1),
                arrival(2, "2", scheduledTime2, actualTime = actualTime2)
            )
        )
        val result = train.hasReachedDestination()
        assertThat(result).isTrue()
    }

    @Test fun `isOrigin() returns true for a the first station`() {
        val result = train.isOrigin(100)
        assertThat(result).isTrue()
    }

    @Test fun `isOrigin() returns false for other than first station`() {
        val result = train.isOrigin(200)
        assertThat(result).isFalse()
    }

    @Test fun `isDestination() returns false for a midpoint station`() {
        val result = train.isDestination(200)
        assertThat(result).isFalse()
    }

    @Test fun `isDestination() returns true for the last station`() {
        val result = train.isDestination(300)
        assertThat(result).isTrue()
    }

    @Test fun `stops() returns the trains timetable rows as a list of stops`() {
        val result = train.stops()
        assertThat(result.size).isEqualTo(3)
        assertThat(result[0].stationCode()).isEqualTo(100)
        assertThat(result[1].stationCode()).isEqualTo(200)
        assertThat(result[2].stationCode()).isEqualTo(300)
    }

    @Test fun `stops() returns empty list for a train with empty timetable`() {
        val result = trainWithEmptyTimetable.stops()
        assertThat(result).isEmpty()
    }

    @Test fun `stops() returns separate stops when origin and destination are the same`() {
        val result = trainWithSameEndpoints.stops()
        assertThat(result).hasSize(2)
        assertThat(result.first().isOrigin()).isTrue()
        assertThat(result.last().isDestination()).isTrue()
    }

    private val trainWithNonCommercialStop = trainWithEmptyTimetable.copy(
        timetable = listOf(
            departure(1, "4", scheduledTime1),
            arrival(2, "2", scheduledTime2, trainStopping = true, commercialStop = false),
            departure(2, "2", scheduledTime3, trainStopping = true, commercialStop = false),
            arrival(3, "2", scheduledTime4)
        )
    )

    @Test fun `commercialStops() returns only the timetable rows marked as commercial stops`() {
        val result = trainWithNonCommercialStop.commercialStops()
        assertThat(result).hasSize(2)
        assertThat(result.first().stationCode()).isEqualTo(1)
        assertThat(result.last().stationCode()).isEqualTo(3)
    }

    private val trainWithNonStopTimetableRows = trainWithEmptyTimetable.copy(
        timetable = listOf(
            departure(1, "4", scheduledTime1),
            arrival(2, "2", scheduledTime2, trainStopping = false, commercialStop = null),
            departure(2, "2", scheduledTime3, trainStopping = false, commercialStop = null),
            arrival(3, "2", scheduledTime4)
        )
    )

    @Test fun `commercialStops() returns only timetable rows marked as stops `() {
        val result = trainWithNonStopTimetableRows.commercialStops()
        assertThat(result).hasSize(2)
        assertThat(result.first().stationCode()).isEqualTo(1)
        assertThat(result.last().stationCode()).isEqualTo(3)
    }

    private val trainNotReady = trainWithEmptyTimetable.copy(
        timetable = listOf(
            departure(1, "1", scheduledTime1, markedReady = false),
            arrival(2, "2", scheduledTime2)
        )
    )

    @Test fun `currentCommercialStop() returns null for a train that is not yet running`() {
        val result = trainNotReady.currentCommercialStop()
        assertThat(result).isNull()
    }

    private val trainReady = trainWithEmptyTimetable.copy(
        timetable = listOf(
            departure(1, "1", scheduledTime1, markedReady = true),
            arrival(2, "2", scheduledTime2)
        )
    )

    @Test fun `currentCommercialStop() returns origin for a train that is marked ready`() {
        val result = trainReady.currentCommercialStop()
        assertThat(result).isNotNull()
        assertThat(result?.isOrigin()).isTrue()
        assertThat(result?.departure?.stationCode).isEqualTo(1)
    }

    @Test fun `stopsAt() for origin returns list of single stop`() {
        val result = train.stopsAt(100)
        assertThat(result).hasSize(1)
        assertThat(result.first().stationCode()).isEqualTo(100)
    }

    @Test fun `stopsAt() for destination return list of single stop`() {
        val result = train.stopsAt(300)
        assertThat(result).hasSize(1)
        assertThat(result.first().stationCode()).isEqualTo(300)
    }

    @Test fun `stopsAt() for a origin and destination returns separate stops`() {
        val result = trainWithSameEndpoints.stopsAt(3)
        assertThat(result).hasSize(2)
        assertThat(result.first().isOrigin()).isTrue()
        assertThat(result.last().isDestination()).isTrue()
    }

    @Test fun `stopsAt() for a station not in timetable returns empty list`() {
        val result = train.stopsAt(400)
        assertThat(result).isEmpty()
    }

    @Test fun `delayCauses() returns an empty list when timetable does not contain any delays`() {
        val result = train.delayCauses()
        assertThat(result).isEmpty()
    }

    private val delayedTrain = trainWithEmptyTimetable.copy(
        timetable = listOf(
            departure(1, "1", Instant.parse("2020-10-10T08:30:00Z")),
            arrival(
                2, "2", Instant.parse("2020-10-10T09:30:00Z"),
                causes = listOf(DelayCause(1))
            ),
            departure(
                2, "2", Instant.parse("2020-10-10T09:35:00Z"),
                causes = listOf(DelayCause(2))
            ),
            arrival(3, "3", Instant.parse("2020-10-10T10:30:00Z"))
        )
    )

    @Test fun `delayCauses() returns a list of delay causes for a delayed train`() {
        val result = delayedTrain.delayCauses()
        assertThat(result).hasSize(2)
        assertThat(result[0]).isEqualTo(DelayCause(1))
        assertThat(result[1]).isEqualTo(DelayCause(2))
    }

    private val trainWithDuplicatedDelayCause = trainWithEmptyTimetable.copy(
        timetable = listOf(
            departure(1, "1", Instant.parse("2020-10-10T08:30:00Z")),
            arrival(
                2, "2", Instant.parse("2020-10-10T09:30:00Z"),
                causes = listOf(DelayCause(1, 2))
            ),
            departure(
                2, "2", Instant.parse("2020-10-10T09:35:00Z"),
                causes = listOf(DelayCause(1, 2))
            ),
            arrival(
                3, "3", Instant.parse("2020-10-10T10:30:00Z"),
                causes = listOf(DelayCause(1, 1))
            )
        )
    )

    @Test fun `delayCauses() returns a list of delay causes without duplicates`() {
        val result = trainWithDuplicatedDelayCause.delayCauses()
        assertThat(result).hasSize(2)
        assertThat(result[0]).isEqualTo(DelayCause(1, 2))
        assertThat(result[1]).isEqualTo(DelayCause(1, 1))
    }

    private val longDistanceTrain = Train(1, "IC", Train.Category.LongDistance)

    @Test fun `isLongDistanceTrain() returns true for a long-distance train`() {
        val result = longDistanceTrain.isLongDistanceTrain()
        assertThat(result).isTrue()
    }

    @Test fun `isCommuterTrain() returns false for a long-distance train`() {
        val result = longDistanceTrain.isCommuterTrain()
        assertThat(result).isFalse()
    }

    private val commuterTrain = Train(2, "L", Train.Category.Commuter, commuterLineId = "A")

    @Test fun `isLongDistanceTrain() returns false for a commuter train`() {
        val result = commuterTrain.isLongDistanceTrain()
        assertThat(result).isFalse()
    }

    @Test fun `isCommuterTrain() returns true for a commuter train`() {
        val result = commuterTrain.isCommuterTrain()
        assertThat(result).isTrue()
    }

}
