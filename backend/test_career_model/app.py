from flask import Flask, request, jsonify
from transformers import AutoTokenizer, AutoModelForSeq2SeqLM
import torch
from flask_cors import CORS 

app = Flask(__name__)
CORS(app) 


MODEL_PATH = "career_model"
tokenizer = AutoTokenizer.from_pretrained(MODEL_PATH)
model = AutoModelForSeq2SeqLM.from_pretrained(MODEL_PATH)
device = "cuda" if torch.cuda.is_available() else "cpu"
model.to(device)

@app.route("/analyze", methods=["POST"])
def analyze():
    data = request.get_json(force=True)
    country = data["country"]
    education = data["education"]
    certification = data["certificate"]
    skills = data["skills"] if "skills" in data else ""
    prompt = (
        "Instruction: Write exactly 5 lines of practical career review and advice for the target country. "
        "Focus on the most impactful improvements to become hireable. Each line is one sentence, no bullets.\n"
        f"Country: {country}\n"
        f"Education: {education}\n"
        f"Certificate: {certification}\n"
    )
    if skills:
        prompt += f"Skills: {skills}\n"

    inputs = tokenizer(prompt, return_tensors="pt", truncation=True, max_length=256).to(device)
    gen = model.generate(
        **inputs,
        max_new_tokens=180,
        num_beams=4,
        no_repeat_ngram_size=3,
        early_stopping=True,
        length_penalty=0.9
    )
    return jsonify({"advice": tokenizer.decode(gen[0], skip_special_tokens=True)})

if __name__ == "__main__":
    app.run(host='0.0.0.0', port=5000, debug=True) 

