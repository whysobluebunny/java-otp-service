package ru.mephi.abondarenko.otpapp.service.notification;

import lombok.extern.slf4j.Slf4j;
import org.smpp.Connection;
import org.smpp.Session;
import org.smpp.TCPIPConnection;
import org.smpp.pdu.BindResponse;
import org.smpp.pdu.BindTransmitter;
import org.smpp.pdu.SubmitSM;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Slf4j
@Service
public class SmsNotificationService {

    private final String host;
    private final int port;
    private final String systemId;
    private final String password;
    private final String systemType;
    private final String sourceAddress;

    public SmsNotificationService() {
        Properties props = loadConfig();
        host = props.getProperty("smpp.host");
        port = Integer.parseInt(props.getProperty("smpp.port"));
        systemId = props.getProperty("smpp.system_id");
        password = props.getProperty("smpp.password");
        systemType = props.getProperty("smpp.system_type");
        sourceAddress = props.getProperty("smpp.source_addr");
    }

    private Properties loadConfig() {
        try {
            Properties props = new Properties();
            props.load(getClass().getClassLoader().getResourceAsStream("sms.properties"));
            return props;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load SMS configuration", e);
        }
    }

    public void sendCode(String destination, String code) {
        Connection connection;
        Session session;

        try {
            // 1. Установка соединения
            connection = new TCPIPConnection(host, port);
            session = new Session(connection);
            // 2. Подготовка Bind Request
            BindTransmitter bindRequest = new BindTransmitter();
            bindRequest.setSystemId(systemId);
            bindRequest.setPassword(password);
            bindRequest.setSystemType(systemType);
            bindRequest.setInterfaceVersion((byte) 0x34); // SMPP v3.4
            bindRequest.setAddressRange(sourceAddress);
            // 3. Выполнение привязки
            BindResponse bindResponse = session.bind(bindRequest);
            if (bindResponse.getCommandStatus() != 0) {
                throw new Exception("Bind failed: " + bindResponse.getCommandStatus());
            }
            // 4. Отправка сообщения
            SubmitSM submitSM = new SubmitSM();
            submitSM.setSourceAddr(sourceAddress);
            submitSM.setDestAddr(destination);
            submitSM.setShortMessage("Your code: " + code);

            session.submit(submitSM);
            log.info("Успешная отправка SMPP");
        } catch (Exception e) {
            log.error("Ошибка SMPP: {}", e.getMessage());
        }
    }
}
