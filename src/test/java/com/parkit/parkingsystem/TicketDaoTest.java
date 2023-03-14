package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import nl.altindag.log.LogCaptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class TicketDaoTest {
    TicketDAO ticketDAO;

    Ticket ticket;

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
        ticket = new Ticket();
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = databaseConfig;

        ticket.setId(3);
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, true));
        ticket.setPrice(1.5);
        ticket.setInTime(new Date());
        ticket.setOutTime(null);

        logCaptor = LogCaptor.forName("TicketDAO");
        logCaptor.setLogLevelToInfo();

        when(databaseConfig.getConnection()).thenReturn(con);
    }

    @Test
     void saveTicketWithExceptionShouldReturnFalse() throws SQLException {

        // Given
        ticket.setVehicleRegNumber("ABCDEF");
        when(con.prepareStatement(DBConstants.SAVE_TICKET)).thenThrow(IllegalArgumentException.class);

        // When
        ticketDAO.saveTicket(ticket);

        // Then
        assertTrue(logCaptor.getErrorLogs().contains("Error fetching next available slot"));

    }

    @Test
     void saveTicketShouldReturnTrue() throws SQLException {

        // Given
        when(con.prepareStatement(DBConstants.SAVE_TICKET)).thenReturn(ps);
        when(ps.execute()).thenReturn(Boolean.TRUE);

        // When
        boolean saved = ticketDAO.saveTicket(ticket);

        // Then
        assertEquals(true, saved);

    }

    @Test
     void getTicketWithExceptionShouldReturnFalse() throws SQLException {

        // Given
        ticket.setVehicleRegNumber("ABCDEF");

        when(con.prepareStatement(DBConstants.GET_TICKET)).thenThrow(IllegalArgumentException.class);

        // When
        ticketDAO.getTicket("ABCDEF");
        // Then
        assertTrue(logCaptor.getErrorLogs().contains("Error fetching next available slot"));

    }
    @Test
     void getTicketShouldReturnFalse() throws SQLException {

        // Given
        when(con.prepareStatement(DBConstants.GET_TICKET)).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(Boolean.FALSE);


        // When
        Ticket res = ticketDAO.getTicket("ABCDEF");

        // Then
        assertNull( res);

    }

    @Test
     void getTicketByIdWithExceptionShouldReturnFalse() throws SQLException {

        // Given
        ticket.setVehicleRegNumber("ABCDEF");

        when(con.prepareStatement(DBConstants.GET_UPDATED_TICKET)).thenThrow(IllegalArgumentException.class);

        // When
        ticketDAO.getTicketById("ABCDEF");
        // Then
        assertTrue(logCaptor.getErrorLogs().contains("Error fetching next available slot"));

    }
    @Test
     void getTicketByIdShouldReturnFalse() throws SQLException {

        // Given
        when(con.prepareStatement(DBConstants.GET_UPDATED_TICKET)).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(Boolean.FALSE);


        // When
        Ticket res = ticketDAO.getTicketById("ABCDEF");

        // Then
        assertNull( res);

    }
    @Test
     void isReccurentExceptionShouldReturnFalse() throws SQLException {

        // Given
        ticket.setVehicleRegNumber("ABCDEF");

        when(con.prepareStatement(DBConstants.GET_RECURRENT)).thenThrow(IllegalArgumentException.class);

        // When
        ticketDAO.isReccurent("ABCDEF");

        // Then
        assertTrue(logCaptor.getErrorLogs().contains("no"));

    }

    @Test
     void isReccurentExceptionShouldReturnTrue() throws SQLException {

        // Given
        when(con.prepareStatement(DBConstants.GET_RECURRENT)).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(Boolean.TRUE);

        // When
        boolean reccurent = ticketDAO.isReccurent("ABCDEF");

        // Then
        assertEquals(true, reccurent);


    }

    @Test
     void updateTicketShouldReturnTrue() throws SQLException {

        // Given
        ticket.setVehicleRegNumber("ABCDEF");
        when(con.prepareStatement(DBConstants.UPDATE_TICKET)).thenThrow(IllegalArgumentException.class);

        // When
        ticketDAO.updateTicket(ticket);

        // Then
        assertTrue(logCaptor.getErrorLogs().contains("Error saving ticket info"));


    }
}