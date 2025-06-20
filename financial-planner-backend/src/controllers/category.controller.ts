import { Request, Response } from "express";
import { AuthRequest } from "../types/request.types";
import database from "../services/database.service";
import { 
    CreateCategoryPayload, 
    UpdateCategoryPayload 
} from "../types/category.types";

export const createCategory = async (req: AuthRequest, res: Response): Promise<void> => {
    try {
        if (!req.user) {
            res.status(401).json({ message: "Authentication required" });
            return;
        }

        const payload: CreateCategoryPayload = {
            ...req.body,
            user_id: req.user.id
        };

        const category = await database.createCategory(payload);
        res.status(201).json({ success: true, data: category });
    } catch (error) {
        console.error("Error creating category:", error);
        res.status(500).json({ 
            success: false, 
            message: "Failed to create category",
            error: process.env.NODE_ENV === 'development' ? error.message : undefined
        });
    }
};

export const getCategories = async (req: AuthRequest, res: Response): Promise<void> => {
    try {
        if (!req.user) {
            res.status(401).json({ message: "Authentication required" });
            return;
        }

        const categories = await database.getCategoryTree(req.user.id);
        res.status(200).json({ success: true, data: categories });
    } catch (error) {
        console.error("Error getting categories:", error);
        res.status(500).json({ 
            success: false, 
            message: "Failed to get categories",
            error: process.env.NODE_ENV === 'development' ? error.message : undefined
        });
    }
};

export const updateCategory = async (req: AuthRequest, res: Response): Promise<void> => {
    try {
        if (!req.user) {
            res.status(401).json({ message: "Authentication required" });
            return;
        }

        const { id } = req.params;
        const payload: UpdateCategoryPayload = req.body;

        const updatedCategory = await database.updateCategory(id, payload);
        res.status(200).json({ success: true, data: updatedCategory });
    } catch (error) {
        console.error("Error updating category:", error);
        res.status(500).json({ 
            success: false, 
            message: "Failed to update category",
            error: process.env.NODE_ENV === 'development' ? error.message : undefined
        });
    }
};

export const deleteCategory = async (req: AuthRequest, res: Response): Promise<void> => {
    try {
        if (!req.user) {
            res.status(401).json({ message: "Authentication required" });
            return;
        }

        const { id } = req.params;
        await database.deleteCategory(id);
        res.status(204).send();
    } catch (error) {
        console.error("Error deleting category:", error);
        res.status(500).json({ 
            success: false, 
            message: "Failed to delete category",
            error: process.env.NODE_ENV === 'development' ? error.message : undefined
        });
    }
};