import os
import cv2
import pandas as pd
import numpy as np
import random
import io
from keras.api.models import load_model
from flask import Flask, request, jsonify, send_file
from werkzeug.utils import secure_filename
from PIL import Image
import os

# Initialize Flask app
app = Flask(__name__)

# Set the upload folder and allowed extensions
UPLOAD_FOLDER = 'received_images'
ALLOWED_EXTENSIONS = {'png', 'jpg', 'jpeg'}

# Ensure the folder exists
os.makedirs(UPLOAD_FOLDER, exist_ok=True)

# Load dataset for handwritten digit generation
def load_dataset():
    train = pd.read_csv("datasets/train.csv", nrows=10000)  # Load only 20000 rows
    x_train = train.iloc[:, 1:].values  # All pixel values
    y_train = train.iloc[:, 0].values   # Corresponding labels
    return x_train, y_train

# Function to check if file extension is allowed
def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

def load_trained_models(models_directory, nets=5):
    models = []
    for j in range(nets):
        model_path = os.path.join(models_directory, f"model_{j}.keras")
        if os.path.exists(model_path):
            print(f"Loading model_{j} from {models_directory}...")
            models.append(load_model(model_path))
        else:
            print(f"Model {j} not found in {models_directory}. Skipping...")
    return models

def preprocess_image(image_path):
    img = cv2.imread(image_path, cv2.IMREAD_GRAYSCALE)
    if img is None:
        raise ValueError(f"Image at {image_path} not found or unable to load.")
    img = cv2.resize(img, (28, 28))  # Resize to 28x28
    img = 255 - img  # Invert the colors
    img = img / 255.0  # Normalize pixel values
    img = img.reshape(1, 28, 28, 1)  # Reshape for model input
    return img

def predict_image(models, image_path):
    img = preprocess_image(image_path)
    results = np.zeros((1, 10))
    for model in models:
        results += model.predict(img)
    probabilities = results / len(models)
    predicted_label = np.argmax(probabilities)
    return predicted_label, probabilities.flatten()

def save_pixel_data(image_path, output_folder):
    img = preprocess_image(image_path)
    flattened_pixels = img.flatten()
    pixel_columns = [f"pixel{i}" for i in range(784)]
    pixel_df = pd.DataFrame([flattened_pixels], columns=pixel_columns)
    image_name = os.path.splitext(os.path.basename(image_path))[0]
    csv_filename = os.path.join(output_folder, f"{image_name}_pixels.csv")
    pixel_df.to_csv(csv_filename, index=False)
    print(f"Pixel data saved to {csv_filename}")

@app.route('/predict', methods=['POST'])
def predict():
    image_file = request.files.get('image')
    if image_file:
        try:
            filename = secure_filename(image_file.filename)
            image_path = os.path.join(UPLOAD_FOLDER, filename)
            image_file.save(image_path)
            img = cv2.imread(image_path, cv2.IMREAD_GRAYSCALE)

            if img is not None:
                # output_folder = "generated_csv_files"
                # os.makedirs(output_folder, exist_ok=True)
                # save_pixel_data(image_path, output_folder)
                label, probabilities = predict_image(models, image_path)

                print(f"Predicted Label: {label}")
                for i, prob in enumerate(probabilities):
                    print(f"Probability of {i}: {prob:.10f}")
                
                label = int(label)
                probabilities = [f"{prob:.10f}" for prob in probabilities]

                response = {
                    'predicted_label': label,
                    'probabilities': probabilities
                }

                return jsonify(response)
            else:
                raise ValueError("Unable to load or process the image properly.")

        except Exception as e:
            return jsonify({'error': f'An error occurred: {str(e)}'}), 400
    else:
        return jsonify({'error': 'No image file provided'}), 400

@app.route('/generate-handwritten-digit', methods=['POST'])
def generate_handwritten_digit():
    x_train, y_train = load_dataset()
    data = request.json
    digit = int(data.get("digit"))
    digit_images = x_train[y_train == digit]

    if len(digit_images) == 0:
        return {"error": "No images found for the requested digit"}, 404

    random_index = random.randint(0, len(digit_images) - 1)
    img = digit_images[random_index].reshape(28, 28)  # Reshape to 28x28

    img_pil = Image.fromarray(img.astype(np.uint8))
    img_byte_arr = io.BytesIO()
    img_pil.save(img_byte_arr, format='PNG')
    img_byte_arr.seek(0)

    return send_file(img_byte_arr, mimetype='image/png', as_attachment=False)

if __name__ == '__main__':
    models_directory = "trained_models"  # Directory where models are saved
    print("Loading trained models...")
    models = load_trained_models(models_directory)

    if not models:
        print("No trained models found. Please train your models first.")
    else:
        app.run(host='0.0.0.0', port=5000, debug=False)  # Run the serve