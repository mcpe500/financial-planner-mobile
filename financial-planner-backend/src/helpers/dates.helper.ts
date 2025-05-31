export function isValidPhoneNumber(phone: string): boolean {
    // Accept Indonesian phone numbers: +62, 08, or 62
    const phoneRegex = /^(\+62|62|0)[0-9]{8,13}$/;
    return phoneRegex.test(phone.replace(/\s+/g, ''));
}

export function isValidDate(dateString: string): boolean {
    // Accept DD/MM/YYYY or YYYY-MM-DD formats
    const ddmmyyyy = /^(\d{2})\/(\d{2})\/(\d{4})$/;
    const yyyymmdd = /^(\d{4})-(\d{2})-(\d{2})$/;

    if (ddmmyyyy.test(dateString)) {
        const [, day, month, year] = dateString.match(ddmmyyyy)!;
        const date = new Date(parseInt(year), parseInt(month) - 1, parseInt(day));
        return date.getFullYear() === parseInt(year) &&
            date.getMonth() === parseInt(month) - 1 &&
            date.getDate() === parseInt(day);
    }

    if (yyyymmdd.test(dateString)) {
        const [, year, month, day] = dateString.match(yyyymmdd)!;
        const date = new Date(parseInt(year), parseInt(month) - 1, parseInt(day));
        return date.getFullYear() === parseInt(year) &&
            date.getMonth() === parseInt(month) - 1 &&
            date.getDate() === parseInt(day);
    }

    return false;
}

export function convertDateFormat(dateString: string): string {
    // Check if it's already in YYYY-MM-DD format
    if (/^\d{4}-\d{2}-\d{2}$/.test(dateString)) {
        return dateString;
    }

    // Convert DD/MM/YYYY to YYYY-MM-DD
    if (/^\d{2}\/\d{2}\/\d{4}$/.test(dateString)) {
        const [day, month, year] = dateString.split('/');
        return `${year}-${month.padStart(2, '0')}-${day.padStart(2, '0')}`;
    }

    // If format is unexpected, throw an error
    throw new Error(`Unexpected date format: ${dateString}`);
}

export function convertDateToDisplay(dateString: string | null): string | null {
    if (!dateString) return null;

    // Convert YYYY-MM-DD to DD/MM/YYYY for mobile app response
    if (/^\d{4}-\d{2}-\d{2}/.test(dateString)) {
        const date = new Date(dateString);
        const day = String(date.getDate()).padStart(2, '0');
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const year = date.getFullYear();
        return `${day}/${month}/${year}`;
    }

    return dateString;
}