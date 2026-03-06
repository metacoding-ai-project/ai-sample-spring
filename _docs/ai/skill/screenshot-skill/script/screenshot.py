import argparse
import json
import os
import subprocess
import sys
import time
import urllib.request
from pathlib import Path

from playwright.sync_api import sync_playwright


def wait_for_server(base_url, timeout=30):
    """서버가 응답할 때까지 대기"""
    start = time.time()
    while time.time() - start < timeout:
        try:
            urllib.request.urlopen(base_url)
            return True
        except Exception:
            time.sleep(1)
    return False


def start_server(project_dir):
    """gradlew bootRun을 백그라운드로 실행"""
    gradlew = os.path.join(project_dir, "gradlew.bat" if sys.platform == "win32" else "gradlew")
    process = subprocess.Popen(
        [gradlew, "bootRun"],
        cwd=project_dir,
        stdout=subprocess.DEVNULL,
        stderr=subprocess.DEVNULL,
    )
    return process


def run_scenarios(base_url, name, output_dir, scenarios):
    """시나리오대로 브라우저 조작 + 스크린샷 캡쳐"""
    Path(output_dir).mkdir(parents=True, exist_ok=True)

    with sync_playwright() as p:
        browser = p.chromium.launch()
        page = browser.new_page(viewport={"width": 1280, "height": 720})

        for i, scenario in enumerate(scenarios, start=1):
            url = base_url + scenario["url"]
            page.goto(url)

            for action in scenario.get("actions", []):
                if action["type"] == "fill":
                    page.fill(action["selector"], action["value"])
                elif action["type"] == "click":
                    page.click(action["selector"])

            wait_ms = scenario.get("wait", 500)
            page.wait_for_timeout(wait_ms)

            screenshot_path = os.path.join(output_dir, f"{name}-{i}.png")
            page.screenshot(path=screenshot_path)
            print(f"Saved: {screenshot_path}")

        browser.close()


def main():
    parser = argparse.ArgumentParser(description="Browser screenshot tool")
    parser.add_argument("--base-url", default="http://localhost:8080")
    parser.add_argument("--name", required=True, help="Screenshot file name prefix")
    parser.add_argument("--output", required=True, help="Output directory for screenshots")
    parser.add_argument("--project-dir", required=True, help="Spring Boot project root")
    parser.add_argument("--scenarios", required=True, help="JSON array of scenarios")
    args = parser.parse_args()

    scenarios = json.loads(args.scenarios)

    # 서버 자동 실행
    print("Starting server...")
    server_process = start_server(args.project_dir)

    try:
        # 서버 대기
        print("Waiting for server to be ready...")
        if not wait_for_server(args.base_url):
            print("ERROR: Server did not start within 30 seconds")
            sys.exit(1)

        print("Server is ready. Capturing screenshots...")
        run_scenarios(args.base_url, args.name, args.output, scenarios)
        print("Done!")
    finally:
        # 서버 종료
        print("Stopping server...")
        server_process.terminate()
        server_process.wait(timeout=10)


if __name__ == "__main__":
    main()
