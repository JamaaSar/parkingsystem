package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

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


    private void setUpPerTest() {
        try {
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
            Ticket ticket = new Ticket();
            ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");
            when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
            when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

            when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to set up test mock objects");
        }
    }
    @Test
    public void processIncomingVehicleTest()  {
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
        when(inputReaderUtil.readSelection()).thenReturn(1);
        parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

        parkingService.processIncomingVehicle();
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    }
   
    @Test
    public void processIncomingVehicleTestThrowException() throws Exception {
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(0);
        when(inputReaderUtil.readSelection()).thenReturn(1);

       parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        Exception exception = assertThrows(Exception.class, () -> {
            parkingService.processIncomingVehicle();
        });


      //  assertThrows(Exception.class, ()->parkingService.processIncomingVehicle());
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains("Error fetching parking number from DB. Parking slots might be full"));

    }
    @Test
    public void processExitingVehicleTest() throws Exception {
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        Ticket ticket = new Ticket();
        ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
        parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        parkingService.processExitingVehicle();
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    }


    @DisplayName("getNextPArkingNumbeRIfAvailable - should return 1")
    @Test
    void getNextParkingNumberCarIfAvailableShouldReturn1() {
        //GIVEN => je prépare mon test
        parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
        //WHEN = > j'execute mon test
        ParkingSpot result = parkingService.getNextParkingNumberIfAvailable();
        //THEN => je vérifie
        assertEquals(1,result.getId());
    }

    @DisplayName("getNextPArkingNumbeRIfAvailable - should return 1")
    @Test
    void getNextParkingNumberBikeIfAvailableShouldReturn2() {
        //GIVEN => je prépare mon test
        parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        when(inputReaderUtil.readSelection()).thenReturn(2);
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE)).thenReturn(2);
        //WHEN = > j'execute mon test
        ParkingSpot result = parkingService.getNextParkingNumberIfAvailable();
        //THEN => je vérifie
        assertEquals(2,result.getId());
    }
//    @DisplayName("getNextPArkingNumbeRIfAvailable - should return 1")
//    @Test
//    void getNextParkingNumberBikeIfAvailableShouldReturn() {
//        //GIVEN => je prépare mon test
//        parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
//        when(inputReaderUtil.readSelection()).thenReturn(3);
//        when(parkingSpotDAO.getNextAvailableSlot(null)).thenReturn(3);
//
//        //WHEN = > j'execute mon test
//        //THEN => je vérifie
//        assertEquals("Incorrect input provided", parkingService.getNextParkingNumberIfAvailable());
//
//    }

}
