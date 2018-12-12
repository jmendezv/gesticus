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

const val PORT_SSL = 465
const val PORT_TLS = 587

class GesticusEmailClient {

    companion object {

        private val props = Properties().apply {

            put("mail.debug", "true")
            put("mail.transport.protocol", "smtp")
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.port", PORT_TLS)
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
        }

        private val authenticator = object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(USER_NAME, USER_PASSWORD.decrypt(SECRET_PASSWORD))
            }
        }

        /*
         *
         * TODO("Test")
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
                message.setRecipients(
                    Message.RecipientType.CC,
                    InternetAddress.parse(address)
                )
            }
            message.setSubject(subject);
            val body = MimeBodyPart()
            body.setText(
                "<img src='https://www.vectorlogo.es/wp-content/uploads/2014/12/logo-vector-generalitat-catalunya.jpg'/><i>$bodyText</i>",
                "utf-8",
                "html"
            )
            val multiPart = MimeMultipart()
            multiPart.addBodyPart(body)
            message.setContent(multiPart)
            Transport.send(message)

        }

        fun sendEmailWithAttatchment(
            subject: String,
            bodyText: String,
            filename: String? = null,
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
                message.setRecipients(
                    Message.RecipientType.CC,
                    InternetAddress.parse(address)
                )
            }
            message.setSubject(subject);
            val body = MimeBodyPart()
            body.setText(
                "<img src='https://www.vectorlogo.es/wp-content/uploads/2014/12/logo-vector-generalitat-catalunya.jpg'/><i>$bodyText</i>",
                "utf-8",
                "html"
            )
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