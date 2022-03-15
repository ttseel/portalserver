# Simulation Portal
## 물류 시뮬레이션 포탈을 개발하는 과정에서 데모로 개발한 프로젝트의 Back-end Repository입니다

사내에서 구현된 서비스와 차이가 있으며, 보안에 저촉되지 않는 선에서 구현하였습니다.

본 서비스는 다음과 같은 기능을 제공합니다

### 도메인 데이터 조회
물류 시뮬레이션과 관련된 다양한 데이터를 수집/가공하여 제공합니다
![반송량 Trend](https://user-images.githubusercontent.com/66378928/158270086-1a966ec7-8c4b-49ce-b05c-f8d71bcdaf33.png)


### 물류 시뮬레이션 서비스
#### 시뮬레이션 Summary 조회
시뮬레이션 API 서버의 부하, 모든 사용자의 시뮬레이션 현황을 조회할 수 있으며, 중지 요청을 할 수 있습니다

![Summary](https://user-images.githubusercontent.com/66378928/158270062-7ebfdbc1-fafb-4bfe-9881-addee03b884b.png)

#### My Simulation 조회
내 시뮬레이션 만을 조회/중지요청을 할 수 있고, 종료된 시뮬레이션의 로그를 다운받을 수 있습니다.

![MySim](https://user-images.githubusercontent.com/66378928/158270073-b6d03bb6-8a97-4a0c-b077-eafda235adb1.png)


#### Simulation 예약 및 구동
시뮬레이션을 예약 할 수 있습니다. 시뮬레이션은 중복 여부, Invalid 데이터 검증 후 예약할 수 있습니다.
이후 서버가 일정 주기로 Resource 체크한 후 구동 가능할 경우 Reservation 리스트에서 Job을 수행합니다.

![Reservation](https://user-images.githubusercontent.com/66378928/158270049-8b59c94e-69af-4ea7-98d9-b72ee2d74502.png)


### 물류 시뮬레이터 다운로드
시뮬레이터 종류, 버전 별 다운로드 기능입니다.

![다운로드](https://user-images.githubusercontent.com/66378928/158270029-76b9cc6a-a084-4746-b6cc-86ccfc27ffe5.png)
