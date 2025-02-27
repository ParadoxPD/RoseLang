default:
	./gradlew run
run:
	rm -rf ./app/build/
	./gradlew run --args="$(DEBUG)" -q --console plain

build:	
	./gradlew build clean

test: 
	./gradlew test 
