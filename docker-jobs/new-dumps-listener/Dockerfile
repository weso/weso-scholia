FROM --platform=linux/amd64 python:3.8
COPY requirements.txt .
RUN pip install -r requirements.txt
COPY . .
ENTRYPOINT ["python", "-u", "main.py"]