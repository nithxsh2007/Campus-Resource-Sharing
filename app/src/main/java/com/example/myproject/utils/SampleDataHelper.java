package com.example.myproject.utils;

import android.content.Context;

import com.example.myproject.model.Resource;
import com.example.myproject.model.ResourceRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SampleDataHelper {

    public interface OnDataSeededListener {
        void onSuccess();
        void onFailure(String error);
    }

    public static void seedSampleResources(Context context, OnDataSeededListener listener) {
        String currentUid = FirebaseHelper.getCurrentUserId();
        if (currentUid == null) {
            if (listener != null) listener.onFailure("User not logged in");
            return;
        }

        FirebaseFirestore db = FirebaseHelper.getFirestore();

        List<Resource> sampleList = new ArrayList<>();

        // 1. Books - Owned by Rahul
        String id1 = db.collection(FirebaseHelper.RESOURCES_COLLECTION).document().getId();
        sampleList.add(new Resource(
                id1,
                "Java Programming & Data Structures",
                "Books",
                "Comprehensive reference book for CS/IT 2nd year students. Covers OOPs concepts, Collections framework, and Data Structures in detail.",
                "Good",
                "Main Library - Study Desk 4",
                "https://images.unsplash.com/photo-1532012197267-da84d127e765?w=500",
                "student_rahul_101",
                "Rahul Sharma",
                "+91 9876543210",
                "available"
        ));

        // 2. Calculators - Owned by Priya
        String id2 = db.collection(FirebaseHelper.RESOURCES_COLLECTION).document().getId();
        sampleList.add(new Resource(
                id2,
                "Scientific Calculator (FX-991EX)",
                "Calculators",
                "Non-programmable scientific calculator permitted in engineering semester exams. 552 functions with dual solar/battery power.",
                "Good",
                "Block A - Room 102",
                "https://images.unsplash.com/photo-1594980596870-8aa52a78d8cd?w=500",
                "student_priya_102",
                "Priya Patel",
                "+91 9876512345",
                "available"
        ));

        // 3. Electronics - Owned by Alex
        String id3 = db.collection(FirebaseHelper.RESOURCES_COLLECTION).document().getId();
        sampleList.add(new Resource(
                id3,
                "Anker 65W Fast USB-C Charger",
                "Electronics",
                "Multi-port GaN fast charger. Can charge MacBooks, Type-C laptops, and smartphones rapidly.",
                "New",
                "Hostel Block 3 - Room 204",
                "https://images.unsplash.com/photo-1583863788434-e58a36330cf0?w=500",
                "student_alex_103",
                "Alex Kumar",
                "+91 9812345678",
                "available"
        ));

        // 4. Accessories - Owned by Karthik
        String id4 = db.collection(FirebaseHelper.RESOURCES_COLLECTION).document().getId();
        sampleList.add(new Resource(
                id4,
                "Mini Drafter & Engineering Drawing Set",
                "Accessories",
                "Complete drawing kit with mini drafter, compass box, set squares, and protective carrying tube for Engineering Graphics lab.",
                "Good",
                "Mechanical Dept - Lab 1",
                "https://images.unsplash.com/photo-1581092160607-ee22621dd758?w=500",
                "student_karthik_104",
                "Karthik V",
                "+91 9786543210",
                "available"
        ));

        // 5. Lab Equipment - Owned by Sneha
        String id5 = db.collection(FirebaseHelper.RESOURCES_COLLECTION).document().getId();
        sampleList.add(new Resource(
                id5,
                "Arduino Uno R3 Ultimate Starter Kit",
                "Lab Equipment",
                "Includes Arduino Uno board, breadboard, jumper wires, ultrasonic sensors, servomotors, LEDs, and LCD display for hardware mini-projects.",
                "New",
                "ECE Dept - Project Lab 2",
                "https://images.unsplash.com/photo-1518770660439-4636190af475?w=500",
                "student_sneha_105",
                "Sneha Reddy",
                "+91 9654321870",
                "available"
        ));

        // 6. Electronics - Owned by Nithish
        String id6 = db.collection(FirebaseHelper.RESOURCES_COLLECTION).document().getId();
        sampleList.add(new Resource(
                id6,
                "Logitech M185 Wireless Optical Mouse",
                "Electronics",
                "Compact 2.4GHz wireless mouse with USB nano receiver. AA battery included, works smoothly on all surfaces.",
                "Used",
                "IT Block - 4th Floor",
                "https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?w=500",
                "student_nithish_106",
                "Nithish R",
                "+91 8838883204",
                "available"
        ));

        // 7. Lab Equipment - Owned by Ananya
        String id7 = db.collection(FirebaseHelper.RESOURCES_COLLECTION).document().getId();
        sampleList.add(new Resource(
                id7,
                "White Cotton Lab Coat (Size L)",
                "Lab Equipment",
                "Clean 100% cotton white lab coat required for Chemistry and Workshop practicals. Size Large.",
                "Good",
                "Chemistry Lab - Ground Floor",
                "https://images.unsplash.com/photo-1584982751601-97dcc096659c?w=500",
                "student_ananya_107",
                "Ananya S",
                "+91 9123456780",
                "available"
        ));

        // 8. Accessories - Owned by Vikram
        String id8 = db.collection(FirebaseHelper.RESOURCES_COLLECTION).document().getId();
        sampleList.add(new Resource(
                id8,
                "Sony Over-Ear Headphones",
                "Accessories",
                "Comfortable wireless Bluetooth headphones with active noise cancelling. Excellent for quiet study sessions in the library.",
                "Good",
                "Library - 2nd Floor Quiet Zone",
                "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500",
                "student_vikram_108",
                "Vikram Singh",
                "+91 9988776655",
                "available"
        ));

        final int[] successCount = {0};
        final int total = sampleList.size();

        for (Resource res : sampleList) {
            db.collection(FirebaseHelper.RESOURCES_COLLECTION)
                    .document(res.getResourceId())
                    .set(res)
                    .addOnSuccessListener(aVoid -> {
                        successCount[0]++;
                        if (successCount[0] == total) {
                            seedSampleNeededRequests(context, listener);
                        }
                    })
                    .addOnFailureListener(e -> {
                        if (listener != null) listener.onFailure(e.getMessage());
                    });
        }
    }

    public static void seedSampleNeededRequests(Context context, OnDataSeededListener listener) {
        String currentUid = FirebaseHelper.getCurrentUserId();
        if (currentUid == null) {
            if (listener != null) listener.onFailure("User not logged in");
            return;
        }

        FirebaseFirestore db = FirebaseHelper.getFirestore();

        List<ResourceRequest> neededList = new ArrayList<>();

        String r1 = db.collection("neededResources").document().getId();
        neededList.add(new ResourceRequest(
                r1,
                "Python Programming for Data Science",
                "Books",
                "Need Python textbook for Semester 4 lab assignments and upcoming CAT exam preparation.",
                "20 Oct 2026",
                "IT Block - Desk 12",
                "student_ananya_107",
                "Ananya S",
                "+91 9123456780",
                "open"
        ));

        String r2 = db.collection("neededResources").document().getId();
        neededList.add(new ResourceRequest(
                r2,
                "USB-C to HDMI Converter Adapter",
                "Electronics",
                "Urgent: Need a USB-C to HDMI converter adapter for seminar presentation in Seminar Hall 2 tomorrow afternoon.",
                "Tomorrow 2:00 PM",
                "CS Block / Seminar Hall 2",
                "student_karthik_104",
                "Karthik V",
                "+91 9786543210",
                "open"
        ));

        String r3 = db.collection("neededResources").document().getId();
        neededList.add(new ResourceRequest(
                r3,
                "Digital Multimeter & Wire Stripper",
                "Lab Equipment",
                "Looking for a portable digital multimeter for circuit testing in Microprocessor Lab project.",
                "18 Oct 2026",
                "ECE Lab 3",
                "student_sneha_105",
                "Sneha Reddy",
                "+91 9654321870",
                "open"
        ));

        String r4 = db.collection("neededResources").document().getId();
        neededList.add(new ResourceRequest(
                r4,
                "Engineering Graphics Mini Drafter",
                "Accessories",
                "Need a mini drafter for drawing exam on Friday morning.",
                "Friday 9:00 AM",
                "Mechanical Drawing Hall",
                "student_alex_103",
                "Alex Kumar",
                "+91 9812345678",
                "open"
        ));

        String r5 = db.collection("neededResources").document().getId();
        neededList.add(new ResourceRequest(
                r5,
                "Casio Scientific Calculator FX-991EX",
                "Calculators",
                "Need scientific calculator for Operations Research exam.",
                "22 Oct 2026",
                "Block B - Room 305",
                "student_rahul_101",
                "Rahul Sharma",
                "+91 9876543210",
                "open"
        ));

        final int[] successCount = {0};
        final int total = neededList.size();

        for (ResourceRequest req : neededList) {
            db.collection("neededResources")
                    .document(req.getRequestId())
                    .set(req)
                    .addOnSuccessListener(aVoid -> {
                        successCount[0]++;
                        if (successCount[0] == total) {
                            if (listener != null) listener.onSuccess();
                        }
                    })
                    .addOnFailureListener(e -> {
                        if (listener != null) listener.onFailure(e.getMessage());
                    });
        }
    }
}
