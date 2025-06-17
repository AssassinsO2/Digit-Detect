# 🔢 DigitDetect – Handwritten Digit Recognition App

**DigitDetect** is a machine learning–powered mobile application that recognizes handwritten digits in real time. The system consists of a **CNN-based prediction model** hosted on the cloud and an Android client that captures input and displays predictions instantly.

---

## 📱 Overview

- 🧠 Trained a Convolutional Neural Network (CNN) on the MNIST dataset with high accuracy
- 🚀 Deployed the model using **Flask REST API** on an **AWS EC2 instance**
- 📲 Android app built with **Java**, sends user-drawn digits via HTTP and receives predictions
- ⚡️ Optimized for speed: average prediction latency under 300ms
- 🎯 Designed for clean, modular integration between ML and mobile platforms

---

## 💡 Features

- Draw digits (0–9) directly in the app
- Instant prediction via cloud-hosted CNN model
- Real-time feedback with confidence scores
- Minimalist UI with smooth user interaction
- Fully open-source and easy to extend

---

## 🧠 Model Details

- **Dataset:** [MNIST](http://yann.lecun.com/exdb/mnist/)
- **Architecture:** 2D Convolutional layers + ReLU + MaxPooling + Dense
- **Accuracy:** ~99% on test set
- **Frameworks:** TensorFlow / Keras
- **Serving:** Flask app with `/predict` POST endpoint

---

## 🧪 Tech Stack

| Layer          | Tech                             |
|----------------|----------------------------------|
| 🧠 ML Model     | TensorFlow, Keras                |
| 🌐 Backend API  | Python, Flask, Gunicorn          |
| ☁️ Deployment   | AWS EC2 (Ubuntu), Nginx, Docker (optional) |
| 📱 Android App | Java, Retrofit, Android Studio   |

---

## 🚀 Getting Started

### 🔧 Clone the Repo

```bash
git clone https://github.com/your-username/DigitDetect.git
cd model/
python train_model.py
# or use saved_model.h5

### 🌐 Run Flask Server
cd server/
python app.py
# Flask will run on http://<your-ip>:5000/predict

### 📲 Build Android App
Open DigitDetectApp folder in Android Studio  
Update the Flask server URL in the Retrofit config  
Build and run on a physical device or emulator

## 📂 Project Structure
DigitDetect/
├── model/                # CNN training and saved model
│   └── train_model.py
├── server/               # Flask REST API
│   └── app.py
├── DigitDetectApp/       # Android mobile app (Java)
│   └── app/
├── README.md
