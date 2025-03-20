import pytest
import sys
import logging
from unittest.mock import MagicMock
from main import handler  # Import the function from your module


def test_handler(caplog):
    # Mock event and context
    mock_event = {}
    mock_context = MagicMock()

    # Capture logs
    with caplog.at_level(logging.ERROR):
        response = handler(mock_event, mock_context)

    # Assertions
    assert response == f"Hello from AWS Lambda using Python{sys.version}!"
    assert "Oupsie an error has occured" in caplog.text
