FROM python:3.9-alpine


COPY requirements.txt /root/requirements.txt

RUN pip install -r /root/requirements.txt

ARG SERVICE_PORT=8080
ENV SERVICE_PORT_ENV=${SERVICE_PORT}

# 4) Copy files
COPY src /root/frontend


# 5) Expose port
EXPOSE ${SERVICE_PORT}

RUN mkdir -p /.streamlit && chmod 777 /.streamlit


# 6) Run webapp

ENTRYPOINT streamlit run /root/frontend/frontend.py --server.port ${SERVICE_PORT_ENV} --logger.level  debug --client.showErrorDetails true --server.enableWebsocketCompression false --server.enableCORS false --server.enableXsrfProtection false
