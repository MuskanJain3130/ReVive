package util;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import io.github.cdimascio.dotenv.Dotenv;

public class SmsService {

    // Load .env variables. If .env is not found, it falls back to system environment variables.
    private static Dotenv loadDotenv() {
        String[] potentialPaths = {
            ".",
            "/Users/muskanjain/Files From d.localized/ReVive/ReVive",
            "d:/ReVive"
        };
        for (String path : potentialPaths) {
            try {
                java.io.File file = new java.io.File(path + "/.env");
                if (file.exists()) {
                    System.out.println("[ReVive Dotenv] Loading .env from: " + file.getAbsolutePath());
                    return Dotenv.configure().directory(path).ignoreIfMissing().load();
                }
            } catch (Exception e) {
                // Ignore and try next path
            }
        }
        return Dotenv.configure().ignoreIfMissing().load();
    }

    private static final Dotenv dotenv = loadDotenv();

    public static String getEnv(String key) {
        String val = dotenv.get(key);
        if (val == null || val.isEmpty()) {
            val = System.getenv(key);
        }
        return val;
    }

    public static final String ACCOUNT_SID = getEnv("TWILIO_ACCOUNT_SID");
    public static final String AUTH_TOKEN = getEnv("TWILIO_AUTH_TOKEN");
    public static final String FROM_NUMBER = getEnv("TWILIO_FROM_NUMBER");
    public static final String FROM_WHATSAPP = getEnv("TWILIO_WHATSAPP_NUMBER");

    static {
        // Initialize Twilio only if the Account SID is present
        if (ACCOUNT_SID != null && !ACCOUNT_SID.isEmpty() && !"YOUR_TWILIO_ACCOUNT_SID".equals(ACCOUNT_SID)) {
            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        }
    }

    /**
     * Sends an SMS using the Twilio API.
     * 
     * @param phone   The destination phone number
     * @param messageText The text message to send
     */
    public static void sendSms(String phone, String messageText) {
        try {
            // Clean the phone number to be just digits
            String cleanPhone = phone.replaceAll("[^0-9]", "");
            
            // Twilio expects numbers in E.164 format: +919999999999 for India
            if (!cleanPhone.startsWith("91") && cleanPhone.length() == 10) {
                cleanPhone = "+91" + cleanPhone;
            } else if (!cleanPhone.startsWith("+")) {
                cleanPhone = "+" + cleanPhone;
            }

            if (ACCOUNT_SID == null || ACCOUNT_SID.isEmpty() || "YOUR_TWILIO_ACCOUNT_SID".equals(ACCOUNT_SID)) {
                System.out.println("[ReVive SMS] WARNING: Twilio Credentials are not set in .env! SMS will not be sent.");
                System.out.println("[ReVive SMS] Message intended for " + cleanPhone + ": " + messageText);
                return;
            }

            System.out.println("[ReVive SMS] Sending real SMS to " + cleanPhone + " via Twilio...");

            Message message = Message.creator(
                    new PhoneNumber(cleanPhone),
                    new PhoneNumber(FROM_NUMBER),
                    messageText
            ).create();

            System.out.println("[ReVive SMS] Twilio Message SID: " + message.getSid());

        } catch (Exception e) {
            System.out.println("[ReVive SMS] Error sending Twilio SMS: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Sends a free-form WhatsApp message (Requires active 24-hour window session).
     * 
     * @param phone   The destination phone number
     * @param messageText The detailed text message (supports emojis and markdown)
     */
    public static void sendWhatsApp(String phone, String messageText) {
        try {
            String cleanPhone = phone.replaceAll("[^0-9]", "");
            if (!cleanPhone.startsWith("91") && cleanPhone.length() == 10) {
                cleanPhone = "+91" + cleanPhone;
            } else if (!cleanPhone.startsWith("+")) {
                cleanPhone = "+" + cleanPhone;
            }

            cleanPhone = "whatsapp:" + cleanPhone;
            
            String fromWhatsApp = FROM_WHATSAPP;
            if (fromWhatsApp == null || fromWhatsApp.isEmpty()) {
                fromWhatsApp = "whatsapp:+14155238886"; // default sandbox
            }

            if (ACCOUNT_SID == null || ACCOUNT_SID.isEmpty() || "YOUR_TWILIO_ACCOUNT_SID".equals(ACCOUNT_SID)) {
                System.out.println("[ReVive SMS] WARNING: Twilio Credentials are not set in .env! WhatsApp will not be sent.");
                return;
            }

            System.out.println("[ReVive WhatsApp] Sending free-form WhatsApp to " + cleanPhone + "...");

            Message message = Message.creator(
                    new PhoneNumber(cleanPhone),
                    new PhoneNumber(fromWhatsApp),
                    messageText
            ).create();

            System.out.println("[ReVive WhatsApp] Twilio WhatsApp Message SID: " + message.getSid());

        } catch (Exception e) {
            System.out.println("[ReVive WhatsApp] Error sending Twilio WhatsApp: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Sends a WhatsApp message using a Twilio Content Template.
     * 
     * @param phone   The destination phone number
     * @param contentSid The Twilio Content Template SID
     * @param contentVariables A JSON string of variables (e.g., "{\"1\":\"val1\",\"2\":\"val2\"}")
     */
    public static void sendWhatsAppTemplate(String phone, String contentSid, String contentVariables) {
        try {
            String cleanPhone = phone.replaceAll("[^0-9]", "");
            if (!cleanPhone.startsWith("91") && cleanPhone.length() == 10) {
                cleanPhone = "+91" + cleanPhone;
            } else if (!cleanPhone.startsWith("+")) {
                cleanPhone = "+" + cleanPhone;
            }

            // Prefix with whatsapp:
            cleanPhone = "whatsapp:" + cleanPhone;
            
            String fromWhatsApp = FROM_WHATSAPP;
            if (fromWhatsApp == null || fromWhatsApp.isEmpty()) {
                fromWhatsApp = "whatsapp:+14155238886"; // default sandbox
            }

            if (ACCOUNT_SID == null || ACCOUNT_SID.isEmpty() || "YOUR_TWILIO_ACCOUNT_SID".equals(ACCOUNT_SID)) {
                System.out.println("[ReVive SMS] WARNING: Twilio Credentials are not set in .env! WhatsApp will not be sent.");
                return;
            }

            System.out.println("[ReVive WhatsApp] Sending WhatsApp to " + cleanPhone + " using template " + contentSid);

            Message message = Message.creator(
                    new PhoneNumber(cleanPhone),
                    new PhoneNumber(fromWhatsApp),
                    "" // Body is empty when using ContentSid
            )
            .setContentSid(contentSid)
            .setContentVariables(contentVariables)
            .create();

            System.out.println("[ReVive WhatsApp] Twilio WhatsApp Message SID: " + message.getSid());

        } catch (Exception e) {
            System.out.println("[ReVive WhatsApp] Error sending Twilio WhatsApp: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
