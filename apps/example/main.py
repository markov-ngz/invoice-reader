import logging
import sys

logger = logging.getLogger(__name__)


def handler(event, context):
    logger.error("Oupsie an error has occured")
    return "Hello from AWS Lambda using Python" + sys.version + "!"
