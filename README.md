# Image Uploader Application

This application is a bit similar to the image gallery. After registration of the user in the application and logging in, the user can view photos uploaded by the administrators. Photos uploaded by administrators are stored in the Cloudinary cloud. Administrators also have the ability to delete photos.

## Functions:
1. Registration and activation of the user via the link in the received e-mail;
2. Viewing photos;
3. Managing your user profile - reviewing your data, changing your data or password, the ability to log out of all other devices, unregister from the portal;
4. Admins can add and remove photos.

## Used technologies and libraries:
1. Java
2. Maven
3. Spring Boot
4. Vaadin
5. Spring Data, JPA, Hibernate
6. Flyway
7. PostgreSQL
8. Spring Security
9. Cloudinary API
10. Lombok
11. Spring Boot Starter Test, JUnit, Mockito
12. CSS

## Environment variables that need to be set:
1. **ADMIN_DEFAULT_EMAIL** - The e-mail address that will be set to the first user (administrator) when the application is launched for the first time. Value example: `abcd@example.com`.
2. **ADMIN_DEFAULT_PASSWORD** - The password that will be set for the first user (administrator) when the application is started for the first time. After the first login as administrator, it is recommended to change its password in the application, leaving the old value of this environment variable. Value example: `password`.
3. **CLOUDINARY_CLOUD_NAME** - **Cloud Name** copied from your Cloudinary Dashboard.
4. **CLOUDINARY_API_KEY** - **API Key** copied from your Cloudinary Dashboard.
5. **CLOUDINARY_API_SECRET** - **API Secret** copied from your Cloudinary Dashboard.
6. **JDBC_DATABASE_URL** - URL to the database. Value example: `jdbc:postgresql://localhost:5432/database_name`.
7. **JDBC_DATABASE_USERNAME** - Database username. Value example: `postgres`.
8. **JDBC_DATABASE_PASSWORD** - Database user password. Value example: `yourPassword`.
9. **LOGGING_FILE_NAME** - Log file path and name. Names can be an exact location (for instance, `C://logs/server.log`) or relative (for instance, `logs/server.log`) to the current directory (project root directory or directory containing packaged war/jar file). You can set an empty value ("" or " " - without quotes) when using only console logs (without saving logs to a file).
10. **MAIL_HOST** - SMTP server host. Value example: `smtp.office365.com`.
11. **MAIL_PORT** - SMTP server port. Value example: `587`.
12. **MAIL_USERNAME** - The username (login) of the mail server. Value example: `example.user@abcde.com`.
13. **MAIL_PASSWORD** - Mail server user password. Value example: `yourPassword`.
14. **TIME_ZONE** - Time zone, for instance `Europe/Warsaw`.
15. **MAX_FILE_SIZE** - Max request or file size. Value example: `100MB`.

## Steps to Setup

#### 1. Configure Cloudinary account

Go to: https://cloudinary.com/. Create a free Cloudinary account (if you don't have one) and log in.Then from the Cloudinary Dashboard, copy the **Cloud Name**, **API Key** and **API Secret** information.

#### 2. Install Node.js LTS

https://nodejs.org/en/download/

#### 3. Clone the repository

```bash
git clone https://github.com/marcinm312/springboot-vaadin-image-uploader.git
```

#### 4. Configure PostgreSQL

First, create a database with any name, e.g. `image_uploader_app`. You will use this name when setting the `JDBC_DATABASE_URL` environment variable. If you name the database as in the previous example, you should set the `JDBC_DATABASE_URL` environment variable as `jdbc:postgresql://localhost:5432/image_uploader_app`.

### Option 1

#### 5. Create a launch configuration in your favorite IDE

Using the example of IntelliJ IDE, select **JDK (Java) version 17**. Select the main class: `pl.marcinm312.springbootimageuploader.SpringbootImageUploaderApplication` and set the environment variables as described above.

#### 6. Run the application using the configuration created in the previous step.

### Option 2

#### 5. Configure the environment variables on your OS as described above

#### 6. Package the application and then run it like so

Type the following commands from the root directory of the project:
```bash
mvn clean package -P production
java -Dfile.encoding=UTF-8 -jar target/springboot-image-uploader-0.0.1-SNAPSHOT.jar
```
In case of problems with building the project, delete the files: **package.json**, **package-lock.json**, from the root directory of the project and repeat the above step again.
