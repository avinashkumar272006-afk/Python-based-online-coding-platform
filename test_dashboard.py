import requests

s = requests.Session()
r2 = s.get("http://localhost:8080/student/dashboard")
print("Dashboard URL:", r2.url)
print("Dashboard Text:\n", r2.text)
