# Vacation Scheduler

## Purpose
The **Vacation Scheduler** application is designed for travelers to organize and manage their vacations and excursions efficiently. The app enables users to create, edit, and delete vacation and excursion details, set alerts for important dates, and share vacation information. It uses the Room Framework for reliable local data storage and retrieval.

---

## How to Operate the Application

### 1. Home Screen: Vacation List
- **View Vacations**: The home screen displays a list of all added vacations.
- **Add a Vacation**: Tap the **"Add Vacation"** button to create a new vacation. Newly created vacations appear on the list.
- **Navigate to Vacation Details**: Tap a vacation on the list to open the **Vacation Details** screen, where you can modify its details.
- **Delete a Vacation**: Vacations can be deleted by tapping the **Delete Button** on the **Home Screen**. Note: Vacations cannot be deleted if associated excursions exist.

### 2. Vacation Details Screen
- **Modify Vacation Details**: Use the following fields to edit the vacation:
    - **Name**: Update the vacation title (e.g., "Spring Break").
    - **Hotel**: Update the place where you are staying.
    - **Start Date & End Date**: Must be in the format **MM/DD/YYYY** (e.g., 03/20/2024). The end date must occur after the start date.
- **Save Changes**: Tap the **"Save"** button to update vacation details in the database.
- **Back Button**: Use the **"Back"** button to return to the home screen without saving changes.
- **Share Vacation**: Use the **"Share"** button to share all vacation details via email, SMS, or clipboard.
- **Vacation Alerts**: Notifications trigger on the vacation start and end dates, displaying the vacation title and whether it is starting or ending. Alerts can be enabled or disabled via your phone’s app settings.

### 3. Excursions
- **View Excursions**: A list of excursions associated with the selected vacation is displayed below the vacation details.
- **Add Excursion**: Tap the **"Add Excursion"** button and enter:
    - **Title**: Excursion name (e.g., "Snorkeling").
    - **Date**: Must be within the vacation’s start and end dates (format: **MM/dd/yyyy**).
- **Edit an Excursion**: Tap on **Edit** next to the excursion to modify its title or date. Ensure the date is in **MM/dd/yyyy** format and falls within the vacation's range.
- **Delete an Excursion**: Tap on **Delete** next to the excursion to remove it.
- **Excursion Alerts**: Notifications trigger on the excursion date, displaying the excursion title. Alerts can be enabled or disabled via your phone’s app settings.

---

## Deployment Information
- **Target Android Version**: Android 8.0 (API Level 26) and higher.
- **Signed APK**: The application is packaged and deployed as a signed APK for production use.

---

## Repository Information
- **Git Repository**: https://gitlab.com/wgu-gitlab-environment/student-repos/agodo11/d308-mobile-application-development-android/

---
