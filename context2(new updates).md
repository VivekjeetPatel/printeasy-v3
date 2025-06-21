{
    "name": "PrintEasy – Automating Printout Process (MVP)",
    "description": "A streamlined, automated document printing solution for small businesses, integrating file upload, real-time printing via an Android app, merchant-specific shop URLs, and integrated payment processing.",
    "tech_stack": {
        "frontend": ["Next.js", "React"],
        "backend": ["Firebase Functions", "Razorpay API"],
        "database_and_storage": ["Firebase Firestore", "Firebase Storage"],
        "android_app": ["Java", "Maven"]
    },
    "system_architecture": {
        "web_portal": {
            "features": [
                "Merchant login and signup page (including shop name, password, UPI ID, cost per page)",
                "Unique shop URLs for each merchant (e.g., `printeasy.neuraltechnologies.in/shop-name`)",
                "Customer file upload page specific to the merchant's URL",
                "Automatic page counting for uploaded documents on the client-side",
                "Razorpay integration for pre-payment based on page count and merchant's cost per page",
                "Payment settlement to the merchant's linked UPI ID via Razorpay",
                "Files uploaded to Firebase Storage ONLY after successful payment",
                "File metadata (name, size, type, page count, payment status) saved to Firestore"
            ]
        },
        "backend_server": {
            "responsibilities": [
                "Handle merchant registration and authentication (Firebase Auth)",
                "Store merchant details (shop name, UPI ID, cost per page) in Firestore",
                "Validate incoming file uploads, ensuring they are linked to an authenticated merchant and payment",
                "Integrate with Razorpay webhooks to confirm successful payments",
                "Receive uploaded files from Next.js frontend (post-payment)",
                "Store files in Firebase Storage",
                "Store metadata in Firestore, associating it with the correct merchant",
                "Immediately forward received files to the specific merchant's Android app for printing (via Firestore listener/Realtime Database)",
                "Clean up old files after 30 minutes (Firebase Functions scheduled tasks)",
                "Delete successfully printed documents from Firebase Storage after 10 minutes (triggered by Android app's print confirmation or a separate status update)"
            ]
        },
        "merchant_android_app": {
            "features": [
                "Merchant login using credentials provided during signup",
                "Receiving files automatically from the backend, specific to the logged-in merchant's shop",
                "Automatically printing received files",
                "Update document status in Firestore to 'printed' upon successful print",
                "Auto-delete files after printing after 10 minutes from storage"
            ]
        }
    },
    "workflow": {
        "1_user_flow": [
            "Customer visits the merchant's unique webpage (e.g., `printeasy.neuraltechnologies.in/shop-name`)",
            "Customer selects and uploads a document",
            "Website automatically counts pages of the document",
            "Website displays total cost based on page count and merchant's cost per page",
            "Customer initiates payment via Razorpay",
            "Upon successful payment, the document is stored in Firebase Storage",
            "Document metadata (including payment confirmation) is stored in Firestore"
        ],
        "2_backend_flow": [
            "Merchant registers and receives a unique shop URL and Android app login code",
            "Backend handles Razorpay payment callback, verifying payment status",
            "If payment is successful, backend receives the file from the frontend",
            "File is stored in Firebase Storage, linked to the specific merchant",
            "File metadata is logged in Firestore, linked to the specific merchant",
            "Backend triggers a notification/update for the corresponding merchant's Android app",
            "Upon receiving print confirmation from the Android app (via Firestore update), a scheduled deletion task is initiated for the document in Firebase Storage after 10 minutes."
        ],
        "3_merchant_flow": [
            "Merchant opens and logs into their PrintEasy Android app using their specific login code",
            "App automatically listens for new print requests for their shop",
            "Upon receiving a new file, the app automatically prints it",
            "After successful printing, the Android app updates the document's status in Firestore to 'printed'."
        ]
    },
    "phases": {
        "phase_1_completed": "Develop simple file upload functionality in Next.js with Firebase integration (initial MVP)",
        "phase_2_current": "Implement merchant authentication (login/signup), unique shop URLs, client-side page counting, and Razorpay payment integration for automated, pre-paid document uploads linked to specific merchant apps.",
        "phase_3_future": "Enhance Android app with print management features, implement real-time status updates (printing, complete), and add administrative dashboard features.",
        
    },
    "goals": {
        "1": "Enable quick, pre-paid file uploads for customers without registration.",
        "2": "Allow merchants to register, get a unique shop URL, and receive/print pre-paid files with minimal effort.",
        "3": "Ensure seamless integration with Razorpay for automated payment collection to merchant UPI IDs.",
        "4": "Provide a secure, automated, and merchant-specific print service for small businesses.",
        "5": "Streamline the payment and document delivery process, eliminating manual intervention."
    }
}
