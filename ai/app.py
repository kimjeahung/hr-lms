from flask import Flask, jsonify, request

app = Flask(__name__)

@app.route("/", methods=["GET"])
def home():
    return "AI Server Running"

@app.route("/health", methods=["GET"])
def health():
    return jsonify({"status": "ok"})

@app.route("/predict", methods=["POST"])
def predict():
    data = request.get_json()
    return jsonify({
        "message": "AI prediction success",
        "input": data
    })

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000)