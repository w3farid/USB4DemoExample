/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package usb4test;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javax.usb.UsbConst;
import javax.usb.UsbControlIrp;
import javax.usb.UsbDevice;
import javax.usb.UsbDeviceDescriptor;
import javax.usb.UsbException;
import javax.usb.UsbHub;
import javax.usb.UsbServices;
import javax.usb.event.UsbDeviceDataEvent;
import javax.usb.event.UsbDeviceErrorEvent;
import javax.usb.event.UsbDeviceEvent;
import javax.usb.event.UsbDeviceListener;
import org.usb4java.javax.Services;

/**
 *
 * @author farid
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    private Label label;

    @FXML
    private void handleButtonAction(ActionEvent event) {
        try {
            UsbServices usbServices = new Services();
            UsbHub usbHub = usbServices.getRootUsbHub();
            short defaultVendorId = (short) Integer.valueOf("0c45", 16).intValue();
            short defaultProductId = (short) Integer.valueOf("7403", 16).intValue();
            UsbDevice usbDevice = findDevice(usbHub, defaultVendorId, defaultProductId);

            
            if (usbDevice != null) {
                System.out.println("Device found");
                
                UsbControlIrp irp = usbDevice.createUsbControlIrp(
                    (byte) (UsbConst.REQUESTTYPE_DIRECTION_IN
                    | UsbConst.REQUESTTYPE_TYPE_STANDARD
                    | UsbConst.REQUESTTYPE_RECIPIENT_DEVICE),
                    UsbConst.REQUEST_GET_CONFIGURATION,
                    (short) 0,
                    (short) 0
            );
            irp.setData(new byte[1]);
            usbDevice.syncSubmit(irp);
            System.out.println(irp.getData()[0]);

                usbDevice.addUsbDeviceListener(new UsbDeviceListener() {
                    @Override
                    public void usbDeviceDetached(UsbDeviceEvent ude) {
                        //To change body of generated methods, choose Tools | Templates.
                        System.out.println("usbDeviceDetached");
                    }

                    @Override
                    public void errorEventOccurred(UsbDeviceErrorEvent udee) {
                        //To change body of generated methods, choose Tools | Templates.
                        System.out.println("errorEventOccurred");
                    }

                    @Override
                    public void dataEventOccurred(UsbDeviceDataEvent udde) {
                        //To change body of generated methods, choose Tools | Templates.
                        System.out.println("dataEventOccurred");
                    }
                });
                System.out.println("You clicked me!");
                label.setText("Hello World!");

            } else {
                System.out.println("Device not found");
            }

        } catch (UsbException ex) {
            Logger.getLogger(FXMLDocumentController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public static UsbDevice findDevice(UsbHub hub, short vendorId, short productId) {
        for (UsbDevice device : (List<UsbDevice>) hub.getAttachedUsbDevices()) {
            UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();
            if (desc.idVendor() == vendorId && desc.idProduct() == productId) {
                return device;
            }
            if (device.isUsbHub()) {
                device = findDevice((UsbHub) device, vendorId, productId);
                if (device != null) {
                    return device;
                }
            }
        }
        return null;
    }

}
