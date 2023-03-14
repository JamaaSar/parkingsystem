package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.util.InputReaderUtil;
import nl.altindag.log.LogCaptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class ParkingSpotDaoTest {

    ParkingSpot parkingSpot;

    ParkingSpotDAO parkingSpotDAO;

    private static LogCaptor logCaptor;

    @Mock
    private Connection con;

    @Mock
    private PreparedStatement ps;

    @Mock
    private ResultSet rs;

    @Mock
    private DataBaseTestConfig databaseConfig;

    @BeforeEach
    void setUp() throws SQLException, ClassNotFoundException {
        parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = databaseConfig;


        logCaptor = LogCaptor.forName("ParkingSpotDAO");
        logCaptor.setLogLevelToInfo();

        when(databaseConfig.getConnection()).thenReturn(con);
    }

    @Test
     void saveTicketWithExceptionShouldReturnFalse() throws SQLException {

        // Given
        when(con.prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT)).thenThrow(IllegalArgumentException.class);

        // When
        parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

        // Then
        assertTrue(logCaptor.getErrorLogs().contains("Error fetching next available slot"));

    }
    @Test
     void saveTicketShouldReturnTrue() throws SQLException {

        // Given
        when(con.prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT)).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(Boolean.TRUE);


        // When
        int res = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

        // Then
        assertNotNull(res);

    }
    @Test
     void saveTicketShouldReturnFalse() throws SQLException {

        // Given
        when(con.prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT)).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(Boolean.FALSE);


        // When
        int res = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

        // Then
        assertEquals(-1, res);

    }
    @Test
     void updateTicketWithExceptionShouldReturnFalse() throws SQLException {

        // Given
        when(con.prepareStatement(DBConstants.UPDATE_PARKING_SPOT)).thenThrow(IllegalArgumentException.class);

        // When
        parkingSpotDAO.updateParking(parkingSpot);

        // Then
        assertTrue(logCaptor.getErrorLogs().contains("Error updating parking info"));

    }
    @Test
     void updateTicketReturnTrue() throws SQLException {

        // Given
        when(con.prepareStatement(DBConstants.UPDATE_PARKING_SPOT)).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);


        // When
        boolean updated = parkingSpotDAO.updateParking(parkingSpot);

        // Then
        assertTrue(updated);

    }
    @Test
     void updateTicketReturnTrueF() throws SQLException {

        // Given
        when(con.prepareStatement(DBConstants.UPDATE_PARKING_SPOT)).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(2);


        // When
        boolean updated = parkingSpotDAO.updateParking(parkingSpot);

        // Then
        assertEquals(false, updated);

    }

}