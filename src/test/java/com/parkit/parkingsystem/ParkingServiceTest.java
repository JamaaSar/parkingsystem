package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import nl.altindag.console.ConsoleCaptor;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;
    private static LogCaptor logCaptor;
    ConsoleCaptor consoleCaptor = new ConsoleCaptor();


    @BeforeEach
    private void setUpPerTest() {

        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        Ticket ticket = new Ticket();
        ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");

        parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        logCaptor = LogCaptor.forRoot();
        logCaptor.setLogLevelToInfo();
    }

    @Test
    public void processIncomingVehicleTest()  {
        //GIVEN
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
        //WHEN
        parkingService.processIncomingVehicle();
        //THEN
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    }
//    @Test
//    public void processIncomingReccurentTest()  {
//        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
//        when(inputReaderUtil.readSelection()).thenReturn(1);
//        when(ticketDAO.isReccurent("ABCDEF")).thenReturn(true);
//
//        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
//        parkingService.processIncomingVehicle();
//        assertTrue(consoleCaptor.getStandardOutput().contains("Welcome back! As a recurring user of our parking lot, you'll benefit from a 5% discount."));
//
//        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
//    }

    @Test
    public void processIncomingVehicleExceptionTest()  {
        //GIVEN
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(0);
        //WHEN
        parkingService.processIncomingVehicle();
        //THEN
        assertTrue(logCaptor.getErrorLogs().contains(("Error fetching next available parking slot")));
    }



    @Test
    public void processExitingVehicleTest() throws Exception {
        //GIVEN
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        Ticket ticket = new Ticket();
        ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
        //WHEN
        parkingService.processExitingVehicle();
        //THEN
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    }
    @Test
    public void processExitingVehicleExceptionTest() throws Exception {
        //GIVEN
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        Ticket ticket = new Ticket();
        ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        when(ticketDAO.getTicket(anyString())).thenReturn(null);
        //WHEN
        parkingService.processExitingVehicle();
        //THEN

        assertTrue(logCaptor.getErrorLogs().contains(("Unable to process exiting vehicle")));
    }
    @Test
    public void processExitingVehicleConsoleTest() throws Exception {
        //GIVEN
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        Ticket ticket = new Ticket();
        ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
        //WHEN
        parkingService.processExitingVehicle();
        //THEN
        assertTrue(consoleCaptor.getStandardOutput().contains("Unable to update ticket information. Error occurred"));
    }

    @Test
    void getNextParkingNumberCarIfAvailableShouldReturn1() {
        //GIVEN => je prépare mon test
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
        //WHEN = > j'execute mon test
        ParkingSpot result = parkingService.getNextParkingNumberIfAvailable();
        //THEN => je vérifie
        assertEquals(1,result.getId());
    }

    @Test
    void getNextParkingNumberBikeIfAvailableShouldReturn2() {
        //GIVEN => je prépare mon test
        when(inputReaderUtil.readSelection()).thenReturn(2);
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE)).thenReturn(2);
        //WHEN = > j'execute mon test

        ParkingSpot result = parkingService.getNextParkingNumberIfAvailable();
        //THEN => je vérifie
        assertEquals(2,result.getId());
    }

    @Test
    public void getNextParkingNumberBikeIfAvailableExceptionTest() throws Exception {
        //GIVEN
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(0);
        //WHEN
        parkingService.getNextParkingNumberIfAvailable();
        //THEN
        assertTrue(logCaptor.getErrorLogs().contains(("Error fetching next available parking slot")));

    }
    @Test
    public void getNextParkingNumberBikeIfAvailableIllegalArgumentExceptionTest() throws Exception {
        //GIVEN
        when(inputReaderUtil.readSelection()).thenReturn(0);
        //WHEN
        parkingService.getNextParkingNumberIfAvailable();
        //THEN
        assertTrue(logCaptor.getErrorLogs().contains(("Error parsing user input for type of vehicle")));

    }

}