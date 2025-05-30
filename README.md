# PrintEasy - Automated Document Printing Solution

PrintEasy is a streamlined, automated document printing solution for small businesses. It allows customers to upload documents through a web interface, which are then automatically printed by a merchant's Android device.

## Features

### Web Application
- Simple, user-friendly document upload interface
- Real-time upload progress tracking
- Secure file storage using Firebase Storage
- Automatic metadata tracking in Firestore

### Android Application
- Automatic document monitoring
- Instant download and printing of new documents
- Status updates and error handling
- Support for various document formats

## Project Structure

```
printeasy-v3/
├── web-app/                 # Next.js web application
│   ├── src/
│   │   ├── app/            # Next.js app directory
│   │   ├── lib/            # Shared utilities
│   │   │   └── firebase.ts # Firebase configuration
│   │   └── ...
│   └── ...
│
└── android-app/            # Android application
    ├── app/
    │   ├── src/main/
    │   │   ├── java/      # Kotlin source files
    │   │   └── res/       # Android resources
    │   └── ...
    └── ...
```

## Setup Instructions

### Web Application Setup

1. Navigate to the web-app directory:
   ```bash
   cd web-app
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm run dev
   ```

4. Access the application at `http://localhost:3000`

### Android Application Setup

1. Open the `android-app` directory in Android Studio

2. Sync the project with Gradle files

3. Ensure you have the following in place:
   - Android SDK installed
   - A connected Android device or emulator
   - Google Services configuration (`google-services.json`)

4. Build and run the application

## Firebase Configuration

The project uses Firebase for:
- File storage (Firebase Storage)
- Real-time database (Firestore)
- Cross-platform communication

Firebase configuration is already set up in:
- Web: `web-app/src/lib/firebase.ts`
- Android: `android-app/app/google-services.json`

## Usage

### For Customers
1. Visit the web application
2. Click "Upload Document"
3. Select your document
4. Wait for the upload to complete
5. Your document will be automatically printed by the merchant

### For Merchants
1. Install the Android application
2. Launch the app
3. Keep the app running in the background
4. Documents will be automatically downloaded and printed when received

## Technical Details

### Web Application
- Built with Next.js 14
- Uses TypeScript for type safety
- Implements Firebase Web SDK
- Features responsive design with Tailwind CSS

### Android Application
- Built with Kotlin
- Uses Firebase Android SDK
- Implements Android Printing Framework
- Features background document monitoring

## Dependencies

### Web Application
- Next.js
- Firebase
- Tailwind CSS
- TypeScript

### Android Application
- Kotlin
- Firebase (Firestore, Storage)
- AndroidX
- Coroutines

## Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details. 