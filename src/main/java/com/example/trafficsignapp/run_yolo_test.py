import torch, numpy as np
from PIL import Image
import torchvision.transforms as T

# 1) Load & prep
model = torch.jit.load("assets/yolov11_android.pt").eval()
transform = T.Compose([
    T.Resize((640, 640)),
    T.ToTensor(),
    T.Normalize(mean=[0.485,0.456,0.406], std=[0.229,0.224,0.225]),
])

img = Image.open("assets/test/stop_sign.jpg").convert("RGB")
inp = transform(img).unsqueeze(0)                  # [1,3,640,640]
out = model(inp).cpu().detach().numpy()[0]         # [66,8400]
raw = out.transpose(1,0).reshape(-1)               # flatten to 8400*66

# 2) Run your parse logic (pseudo–code in Python)
boxes, scores, class_ids = [], [], []
row_len = 66
num_preds = raw.size // row_len
for i in range(num_preds):
    offset = i * row_len
    cx, cy, w, h, obj = raw[offset:offset+5]
    class_scores = raw[offset+5:offset+5+61]
    best_cls = int(np.argmax(class_scores))
    conf = obj * class_scores[best_cls]
    if conf < 0.4: continue
    # Convert normalized coords → pixel coords on original image size
    left   = (cx - w/2)*img.width
    top    = (cy - h/2)*img.height
    right  = (cx + w/2)*img.width
    bottom = (cy + h/2)*img.height
    boxes.append((left, top, right, bottom))
    scores.append(conf)
    class_ids.append(best_cls)

print(f"Found {len(boxes)} boxes above 0.4 confidence")
for b,s,c in zip(boxes, scores, class_ids):
    print(f"  Class {c} ({s:.2f}): {b}")