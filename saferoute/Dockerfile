FROM hellices/saferoute-base

EXPOSE 8080/tcp

COPY ./*.py /app/
COPY ./model /app/model/

WORKDIR /app

ENTRYPOINT ["python", "main.py"]
