import { Request, Response } from "express";

interface ValidationController {
    validateToken: (request: Request, response: Response) => void;
}

export const Validator: ValidationController = {
    validateToken: (request: Request, response: Response) => {
        const token: string | null = request.body.token;
        token === "good token" ?
            response.json(
                {
                    userId: "goodUserId",
                    ttl: 1800,
                    scopes: ["*"]
                }
            ) : response.status(403).json(
                {
                    badToken: token,
                    realm: "authenticated-service"
                }
            )
    }
}