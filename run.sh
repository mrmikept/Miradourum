#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${GREEN}Starting Miradourum Application Stack...${NC}"

# Cleanup function
cleanup() {
    echo -e "\n${YELLOW}Shutting down...${NC}"
    if [ ! -z "$FRONTEND_PID" ]; then
        kill $FRONTEND_PID 2>/dev/null
    fi
    cd miradourum && docker-compose down
    exit 0
}

# Handle Ctrl+C
trap cleanup SIGINT SIGTERM

# Start backend
echo -e "${GREEN}Starting backend services...${NC}"
cd miradourum
docker-compose up -d

# Start frontend in background
echo -e "${GREEN}Starting frontend...${NC}"
if cd ../miradourum-front; then
    npm install
    npm run dev &
    FRONTEND_PID=$!
else
    echo -e "${RED}Frontend directory not found${NC}"
    ls -la ../
    cleanup
fi

# Start Spring Boot app
echo -e "${GREEN}Starting Spring Boot application...${NC}"
cd ../miradourum

if [ -f "./mvnw" ]; then
    ./mvnw spring-boot:run -DskipTests
else
    mvn spring-boot:run -DskipTests
fi

# Cleanup when Spring Boot exits
cleanup