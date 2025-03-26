default:
	./gradlew run
run:
	rm -rf ./app/build/
	./gradlew run --args="$(ARGS)" -q --console plain

build:	
	./gradlew build clean

test: 
	./gradlew test 
