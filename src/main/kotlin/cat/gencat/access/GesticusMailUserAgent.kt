package cat.gencat.access

import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

const val USER_NAME = "fpestades@xtec.cat"
const val USER_PASSWORD = "8lMEuDlsEyZUuskwrSeecVKF/1bUDcEk"

const val SECRET_PASSWORD = "secret"

const val PORT_OLD_SSL = 465
const val PORT_TLS_SUBMISSION = 587

/*
*
* First ever email 1971
*
* SSL and TLS both provide a way to encrypt a communication channel between two computers.
*
* TLS is the successor of SSL and both terms are used interchangeably in general.
*
* STARTTLS is a way to tacke an existing insecure connections and upgrade it to a secure
* connection using SSL/TLS.
*
* Version numbering is inconsistent between SSL and TLS. When TLS took over from SSL as the
* preferred protocol name, it began a new version number, and also began using sub-versions.
*
* SSL v2, SSL v3, TLS v1.0, TLS v1.1, TLS v1.2, TLS v.1.3
*
* SMTP (Simple Mail Protocol) uses port 23 and was originally designed for transfer, not submission.
*
* So yet another port (587) was defined for message submission.
*
* In most cases, systems that offer message submission over port 587 require clients to use STARTTLS
* to upgrade the connections and also require a username and password to authenticate.
*
* */

class GesticusMailUserAgent {

    companion object {

        private val props = Properties().apply {

            put("mail.debug", "true")
            // SMTP
            put("mail.transport.protocol", "smtp")
            // Free GMAIL smtp server
            put("mail.smtp.host", "smtp.gmail.com")
            // Free Hotmail stmp server
            // put("mail.smtp.host", "smtp.live.com")
            put("mail.smtp.port", PORT_TLS_SUBMISSION)
            put("mail.smtp.auth", "true")
            // STARTTLS
            put("mail.smtp.starttls.enable", "true")
            // SSL
//            put("mail.smtp.socketFactory.port", "465")
//            put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")


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
            message.sentDate = Date()
            message.setFrom(InternetAddress(USER_NAME))
            message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(USER_NAME, true)
            )

            for (address in addresses) {
                message.setRecipients(
                    Message.RecipientType.CC,
                    InternetAddress.parse(address, true)
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