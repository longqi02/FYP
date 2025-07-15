import torch

# Load your model (make sure the .pt file is in the same folder)
model = torch.jit.load("C:/Users/sadca/AndroidStudioProjects/TrafficSignAPP/app/src/main/assets/yolov11_android.pt")
model.eval()

# Create dummy input (based on YOLO standard 640x640)
dummy_input = torch.randn(1, 3, 640, 640)

# Forward pass
with torch.no_grad():
    output = model(dummy_input)

# Print output shape
print("Output shape:", output.shape)