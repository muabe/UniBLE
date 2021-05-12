# UniBLE
내가 Ble 관련되서 라이브러리를 만든 이유는 이렇다. <br>
가끔씩 아르바이트로 블루투스 관련 일들이 꽤 들어온다. 블루투스 일이 아르바이트로는 꿀 알바이다. <br><br>

### 블루투스가 꿀알바인 이유는 
 - 하는 사람이 많지 않다 -> 고로 고비용을 받는다
 - 블루투스 일들이 대부분 샘플링이나 시연을 목적으로 제작한다.
 - 샘플링이 목적이다 보니 향후 유지보수 우려가 없다.(종료후 귀찮은 일이 안생긴다)
 - 또한 돌아가는 것만 확인하면 되며 완성도가 낮아도 된다(오류 및 예외에 관대)
 - 항산 긴급하게 들어와서 기간이 짧다. 그래서 고기능은 포기하거나 버림 -> 기간 대비 수익이 좋다.

<br>

### 하지만 Ble 개발은 두렵다.
 - 일단 Ble 겁나 복잡하다. 개념이 어렵고 이해하기가 힘듬. -> 개발 힘듬
 - 일이 항상 급하게 진행되다 보니 문서나 필수로 정의 되야 하는것 들이 부족하다. 
 - Server 개발자, 프로젝트관리자, 나를 포함하여 Ble 지식이 부족하다 보니 많이 헤맨다.
 - 보통 Ble는 임베디드 기반이기 떄문에(장비) 커뮤니케이션이 정말 어렵다.
 - 임베디드 기반이기 때문에 디버깅 최악이다. 그래서 스스로 문제를 찾아 해결 해야한다.

<br>

### 해결책을 찾으려 고민했다.
이처럼 고수익에 가성비 좋은 알바지만 항상 두려운 개발이 Ble 였다. <br>
하지만 힘든건 제쳐두고 꿀만 빨고 싶은것이 사람 마음인지라 어떤 것들이 해결되면 좋을지 생각 해봤다.
 - 어려운 Ble 개념을 몰라도 개발했으면 좋겠다.
 - 여러 루트에서 불려지는 콜백 방식의 복잡한 개발을 쉽게 하면 좋겠다.
 - 커뮤니케이션이 이해하기 쉬운 소스 레벨로 이루어 졌으면 좋겠다.
 - 장비의 디버깅을 해줄수 있게 해줬으면 좋겠다.

<br>
 
### 이렇게 하면 해결되지 않을까?
위의 4가지 문제를 해결하고 쉽게 Ble 개발을 하려고 만든것이 UniBLE 이다.
 - UniBle는 Service -> characteristic -> descriptor 기본 Ble 계층 구조만 알면 개발이 가능하다.
 - 뒤엉켜 있는 여러가지 콜백을 상식적인 Process 흐름에 맞는 용도별로 분리하여 인터페이스를 제공한다.
 - Service에서 characteristic로 이어지고 descriptor로 이어지는 프로토콜의 흐름을 소스코드의 흐름과 같은
    프레임을 제공하여 이해하기 쉽고 코드 레벨의 커뮤니케이션으로 이루어 질수 있다.
 - 디버깅을 위해 믿을 수 있는 프레임이 필요한데 UniBle는 기능을 새롭게 만들거나 추가한 것이 아닌 
    패턴만을 정의한 것이기에 오류에 대한 신뢰성이 있다. 
    
### 가벼운 마음으로 만든 UniBLE 
이처럼 UniBLE는 어렵고 복잡한 기능의 서비스를 만들기 위한 라이브러리가 아니다. <br>
열악한 환경에서 빠르고 쉽게 Ble를 개발하기 위한 목적으로 만들어 졌다.
