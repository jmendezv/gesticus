package cat.gencat.access.email

import cat.gencat.access.functions.decrypt
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

const val USER_NAME = "fpestades@xtec.cat"
const val USER_PASSWORD = "8lMEuDlsEyZUuskwrSeecVKF/1bUDcEk"

const val SECRET_PASSWORD = "secret"

const val PORT_SSL = "465"
const val PORT_TLS = "587"

class GesticusEmailClient {

    companion object {

        private val props = Properties().apply {

            put("mail.debug", "true")
            put("mail.transport.protocol", "smtp")
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.port", PORT_SSL)
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")

            put("mail.smtp.socketFactory.port", PORT_SSL); //SSL Port
            put(
                "mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory"
            ); //SSL Factory Class
        }

        private val authenticator = object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(USER_NAME, USER_PASSWORD.decrypt(SECRET_PASSWORD))
//                return PasswordAuthentication(USER_NAME, USER_PASSWORD)
            }
        }

        /*
         *
         * Sends a message to multiple recipients.
         *
         * Max 99
         *
         * */
        fun sendEmail(
            subject: String,
            bodyText: String,
            vararg addresses: String
        ): Unit {

            val session = Session.getInstance(props, authenticator)

            val message: MimeMessage = MimeMessage(session)
            message.setFrom(InternetAddress(USER_NAME))
            message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(USER_NAME)
            )
            for (address in addresses) {
                message.addRecipients(Message.RecipientType.BCC, InternetAddress.parse(address))
            }
            message.subject = subject;
            val body = MimeBodyPart()
            body.setContent(bodyText, "text/html; charset=utf-8")
            val multiPart = MimeMultipart()
            multiPart.addBodyPart(body)
            message.setContent(multiPart)
            Transport.send(message)
        }

        /*
         *
         * Sends a message to multiple recipients
         *
         * */
        fun sendBulkEmail(
            subject: String,
            bodyText: String,
            addresses: List<String>
        ): Unit {

            val session = Session.getInstance(props, authenticator)

            val message: MimeMessage = MimeMessage(session)
            message.setFrom(InternetAddress(USER_NAME))
            message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(USER_NAME)
            )
            for (address in addresses) {
                message.addRecipients(Message.RecipientType.BCC, InternetAddress.parse(address))
            }
            message.subject = subject;
            val body = MimeBodyPart()
            body.setContent(bodyText, "text/html; charset=utf-8")
            val multiPart = MimeMultipart()
            multiPart.addBodyPart(body)
            message.setContent(multiPart)
            Transport.send(message)
        }

        /*
        *
        * Sends a message with attachment to multiple recipients
        *
        * */
        fun sendEmailWithAttatchment(
            subject: String,
            bodyText: String,
            filename: String? = null,
            vararg addresses: String
        ): Unit {

            System.setProperty("java.net.preferIPv4Stack", "true")

            val session = Session.getInstance(props, authenticator)

            val message: MimeMessage = MimeMessage(session)
            message.setFrom(InternetAddress(USER_NAME))
            message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(USER_NAME)
            )
            for (address in addresses) {
                message.addRecipients(Message.RecipientType.BCC, InternetAddress.parse(address))
            }
            message.subject = subject;
            val body = MimeBodyPart()
            // setText(....) is like setContent(..., "text/plain")
            // body.setText(bodyText)
            body.setContent(bodyText, "text/html; charset=utf-8")
            val multiPart = MimeMultipart()
            multiPart.addBodyPart(body)
            val attachment = MimeBodyPart()
            // Use DataSource with javax 1.3
            // val dataSource = FileDataSource(filename)
            // Only javax 1.4+
            attachment.attachFile(filename)
            multiPart.addBodyPart(attachment)
            message.setContent(multiPart)
            Transport.send(message)
        }

        /*
         *
         * Sends a message with attachment to multiple recipients.
         *
         * This method schedules emails
         *
         * */
        fun sendBulkEmailWithAttatchment(
            subject: String,
            bodyText: String,
            filename: String? = null,
            addresses: List<String>
        ): Unit {

            System.setProperty("java.net.preferIPv4Stack", "true")

            val session = Session.getInstance(props, authenticator)

            val message: MimeMessage = MimeMessage(session)
            message.setFrom(InternetAddress(USER_NAME))
            message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(USER_NAME)
            )
            for (address in addresses) {
                message.addRecipients(Message.RecipientType.BCC, InternetAddress.parse(address))
            }
            message.subject = subject;
            val body = MimeBodyPart()
            // setText(....) is like setContent(..., "text/plain")
            // body.setText(bodyText)
            body.setContent(bodyText, "text/html; charset=utf-8")
            val multiPart = MimeMultipart()
            multiPart.addBodyPart(body)
            val attachment = MimeBodyPart()
            // Use DataSource with javax 1.3
            // val dataSource = FileDataSource(filename)
            // Only javax 1.4+
            attachment.attachFile(filename)
            multiPart.addBodyPart(attachment)
            message.setContent(multiPart)
            Transport.send(message)
        }

    }
}