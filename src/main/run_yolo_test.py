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


# 1) Inference + reshape
raw = model(inp).cpu().detach()[0]   # shape [66,8400]
raw = raw.permute(1, 0)              # shape [8400, 66]

# ─── APPLY SIGMOID TO LOGITS ───────────────────────────────────
# objectness is at index 4; class logits start at index 5
obj_logit = raw[:, 4]
cls_logits = raw[:, 5:]

obj_conf = torch.sigmoid(obj_logit).numpy()       # now [0,1]
cls_confs = torch.sigmoid(cls_logits).numpy()     # now [0,1] per class

# 2) Inspect top-5 objectness
top5 = np.argsort(obj_conf)[-5:][::-1]
print("Top-5 objectness after sigmoid:")
for idx in top5:
    print(f" Anchor {idx:4d}: obj={obj_conf[idx]:.3f}")

# 3) Re-run your parse logic with the activated scores
boxes, scores, class_ids = [], [], []
for i in range(raw.shape[0]):
    conf = obj_conf[i]
    class_scores = cls_confs[i]
    best_c = int(class_scores.argmax())
    final_score = conf * class_scores[best_c]
    if final_score < 0.4: 
        continue

    cx, cy, w, h = raw[i, :4].numpy()
    left   = (cx - w/2) * img.width
    top    = (cy - h/2) * img.height
    right  = (cx + w/2) * img.width
    bottom = (cy + h/2) * img.height

    boxes.append((left, top, right, bottom))
    scores.append(final_score)
    class_ids.append(best_c)

print(f"Found {len(boxes)} boxes above 0.4 confidence")
for b,s,c in zip(boxes, scores, class_ids):
    print(f" Class {c} ({s:.2f}): {b}")