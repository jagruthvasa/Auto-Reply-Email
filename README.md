# Auto-Reply Email

## Project Overview

The Auto-Reply Email project is designed to automatically respond to incoming emails with a professional acknowledgment message. This project uses Java with Spring Boot for backend processing, handling emails with Jakarta Mail, and integrating with the Google Gemini API to generate responses.

## Features

- Automatically responds to incoming emails.
- Generates acknowledgment messages using the Google Gemini API.

## Prerequisites

- Java 22
- Gradle 7.0 or higher
- Access to an IMAP email account (e.g., Gmail)
- Make sure to enable IMAP in your gmail settings
- Make sure to create the app password for your gmail

## Getting Started

Follow these steps to set up and run the project locally:

### 1. Clone the Repository

```bash
git clone https://github.com/jagruthvasa/Auto-Reply-Email.git
cd Auto-Reply-Email
```

### 2. Configure Email Credentials

Make sure to add below properties in this file `src/main/resources/application.properties`

```properties
email.address=your-email@gmail.com
email.password=your-email-password
gemini.api.key=your-gemini-api-key
gemini.api.url=https://api.gemini.com
```

### 3. Build the Project

Use Gradle to build the project:

```bash
./gradlew build
```

### 4. Run the Application

Start the application using Gradle:

```bash
./gradlew bootRun
```

## Usage

- The application will automatically check for new emails every 30 seconds.
- When a new email is detected, it will generate a professional acknowledgment response and send it back to the sender.

## API Integration
- The project integrates with the Google Gemini API to generate email responses. Ensure that you have a valid API key and URL configured in the application.properties file.

## Sample Output

- This is the email
  
  ![image](https://github.com/user-attachments/assets/529618f2-7f02-44b5-bd37-9b5205ce3543)

- Automated Response of the email
  
  ![image](https://github.com/user-attachments/assets/9cbf4d38-3276-4a6a-a59b-25faea4d7ea8)


## Contact 📞

Vasa Sai Jagruth - [@LinkedIn](https://www.linkedin.com/in/jagruth/) - jagruthvasa@gmail.com - 9010545613

