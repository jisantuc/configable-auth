import express, { Request, Response } from 'express';
import { Validator } from '../controllers';

export const router = express.Router({
    strict: true
});

router.post("/", Validator.validateToken)